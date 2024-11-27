package nl.bertriksikken.stofradar.geolocation;

import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.inmemory.request.InMemorySlidingWindowRequestRateLimiter;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;

/**
 * You can test this with curl:
 * curl -H "Content-Type: application/json"
 * -d @stofradar/src/test/resources/GeoLocationRequest.json http://localhost:9000/v1/geolocate -v
 */
@Singleton
public class GeoLocationResource implements IGeoLocationResource {

    private static final Logger LOG = LoggerFactory.getLogger(GeoLocationResource.class);
    private static final String LIMIT_KEY = "geolocation";
    private final IGeoLocator geoLocator;
    private final InMemorySlidingWindowRequestRateLimiter limiter;

    public GeoLocationResource(IGeoLocator geoLocator) {
        this.geoLocator = Objects.requireNonNull(geoLocator);
        RequestLimitRule rule = RequestLimitRule.of(Duration.ofMinutes(10), 10).withPrecision(Duration.ofMinutes(1));
        limiter = new InMemorySlidingWindowRequestRateLimiter(Set.of(rule));
    }

    @Override
    public GeoLocationResponse geoLocate(GeoLocationRequest request) {
        if (limiter.overLimitWhenIncremented(LIMIT_KEY)) {
            throw new ClientErrorException(Response.Status.TOO_MANY_REQUESTS);
        }

        LOG.info("Got request: {}", request);
        try {
            GeoLocationResponse response = geoLocator.geoLocate(request);
            if (!response.isValid()) {
                throw new NotFoundException();
            }
            return response;
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
