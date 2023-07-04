package nl.bertriksikken.stofradar.restapi;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import nl.bertriksikken.stofradar.render.SensorValue;

public final class AirRestApi implements IAirRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(AirRestApi.class);
    private static final double KM_PER_DEGREE_LAT = 40075.0 / 360.0;

    private static ExecutorService executor;
    private static double maxd = 10;
    private static Map<String, SensorValue> dataStore = new HashMap<>();
    private static RequestRateLimiter rateLimiter;

    public static void initialize(ExecutorService executorService, double radius, RequestRateLimiter limiter) {
        executor = Objects.requireNonNull(executorService);
        maxd = radius;
        rateLimiter = Objects.requireNonNull(limiter);
    }

    // updates the sensor values, runs on the (single-thread) executor, so is
    // thread-safe with REST requests
    public static void updateSensorValues(Map<String, SensorValue> sensorValues) {
        executor.execute(() -> {
            dataStore.clear();
            dataStore.putAll(sensorValues);
        });
    }

    @Override
    public AirResult getAir(String userAgent, double latitude, double longitude) {
        // rate limit
        if (rateLimiter.overLimitWhenIncremented(userAgent)) {
            LOG.info("Denied PM calculation (rate limited), location {}/{}, user '{}'", latitude, longitude, userAgent);
            return null;
        }
        // schedule for immediate execution
        try {
            return executor.submit(() -> doGetAir(userAgent, latitude, longitude)).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Caught exception", e);
            return null;
        }
    }

    private AirResult doGetAir(String userAgent, double latitude, double longitude) {
        Instant start = Instant.now();

        // take a snapshot of values
        List<SensorValue> values = new ArrayList<>(dataStore.values());

        // convert to km
        values = convertToKm(values, latitude, longitude);

        // roughly filter box around center
        values = values.stream().filter(v -> (v.x > -maxd) && (v.x < maxd) && (v.y > -maxd) && (v.y < maxd))
                .filter(v -> (v.value >= 0)).collect(Collectors.toList());

        // sort by distance
        values.sort(this::compareByDistance);

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
        return values.stream().map(v -> new SensorValue(v.id, (v.x - longitude) * kmPerDegreeLon,
                (v.y - latitude) * KM_PER_DEGREE_LAT, v.value, v.time)).collect(Collectors.toList());
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
