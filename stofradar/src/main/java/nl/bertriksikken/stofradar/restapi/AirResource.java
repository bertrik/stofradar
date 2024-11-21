package nl.bertriksikken.stofradar.restapi;

import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import es.moki.ratelimitj.inmemory.request.InMemorySlidingWindowRequestRateLimiter;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import nl.bertriksikken.stofradar.render.SensorValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public final class AirResource implements IAirResource {

    private static final Logger LOG = LoggerFactory.getLogger(AirResource.class);
    private static final double KM_PER_DEGREE_LAT = 40075.0 / 360.0;

    private static Map<String, SensorValue> dataStore = new HashMap<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final RequestRateLimiter limiter;
    private final AirRestApiConfig config;

    public AirResource(AirRestApiConfig config) {
        RequestLimitRule rule = RequestLimitRule.of(Duration.ofSeconds(60), 10).withPrecision(Duration.ofSeconds(3));
        this.limiter = new InMemorySlidingWindowRequestRateLimiter(Set.of(rule));
        this.config = Objects.requireNonNull(config);
    }

    public void start() {
        LOG.info("Starting air resource");
    }

    public void stop() {
        executor.shutdown();
        LOG.info("Stopped air resource");
    }

    // swap in new data (hacky)
    public static void updateSensorValues(Map<String, SensorValue> sensorValues) {
        dataStore = Map.copyOf(sensorValues);
    }

    @Override
    public AirResult getAirLegacy(String userAgent, double latitude, double longitude) {
        return getAir(userAgent, latitude, longitude);
    }

    @Override
    public AirResult getAir(String userAgent, double latitude, double longitude) {
        // rate limit
        if (limiter.overLimitWhenIncremented(userAgent)) {
            LOG.info("Denied PM calculation (rate limited), location {}/{}, user '{}'", latitude, longitude, userAgent);
            throw new ClientErrorException("Rate limit reached", 419);
        }

        // schedule for immediate execution
        try {
            return executor.submit(() -> doGetAir(userAgent, latitude, longitude)).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Caught exception", e);
            throw new InternalServerErrorException();
        }
    }

    private AirResult doGetAir(String userAgent, double latitude, double longitude) {
        Instant start = Instant.now();

        // take a snapshot of values
        List<SensorValue> values = new ArrayList<>(dataStore.values());

        // convert to km
        values = convertToKm(values, latitude, longitude);

        // roughly filter box around center, sort by distance
        double maxd = config.getMaxDistance();
        values = values.stream().filter(v -> (v.x > -maxd) && (v.x < maxd) && (v.y > -maxd) && (v.y < maxd))
                .filter(v -> (v.value >= 0)).sorted(this::compareByDistance).toList();

        // calculate inverse distance weighted value
        double value = calculateIDW(values);
        AirResult result = new AirResult(value);

        // add list of closest sensors (up to 3)
        values.stream().limit(3).forEach(result::addSensor);

        long ms = Duration.between(start, Instant.now()).toMillis();
        LOG.info("Calculated PM {} in {} ms, location {}/{}, user '{}'", result, ms, latitude, longitude, userAgent);
        LOG.info("Google maps link: https://maps.google.com/?q={},{}", latitude, longitude);
        return result;
    }

    private List<SensorValue> convertToKm(Collection<SensorValue> values, double latitude, double longitude) {
        double kmPerDegreeLon = Math.cos(Math.toRadians(latitude)) * KM_PER_DEGREE_LAT;
        return values.stream().map(v -> new SensorValue(v.source, v.id,
                (v.x - longitude) * kmPerDegreeLon, (v.y - latitude) * KM_PER_DEGREE_LAT, v.value, v.time)).toList();
    }

    private double calculateIDW(List<SensorValue> values) {
        double sum_pm = 0.0;
        double sum_w = 0.0;
        for (SensorValue value : values) {
            double d2 = (value.x * value.x) + (value.y * value.y);
            if (d2 > 0.0) {
                double w = 1.0 / d2;
                sum_pm += w * value.value;
                sum_w += w;
            } else {
                return value.value;
            }
        }
        return sum_pm / sum_w;
    }

    @Override
    public InputStream getFavicon() {
        return getClass().getClassLoader().getResourceAsStream("favicon.ico");
    }

    private int compareByDistance(SensorValue v1, SensorValue v2) {
        return Double.compare((v1.x * v1.x) + (v1.y * v1.y), (v2.x * v2.x) + (v2.y * v2.y));
    }

}
