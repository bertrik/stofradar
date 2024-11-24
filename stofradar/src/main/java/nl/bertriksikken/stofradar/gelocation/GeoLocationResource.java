package nl.bertriksikken.stofradar.gelocation;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * You can test this with curl:
 * curl -H "Content-Type: application/json"
 *  -d @stofradar/src/test/resources/GeoLocationRequest.json http://localhost:9000/v1/geolocate -v
 */
@Singleton
public class GeoLocationResource implements IGeoLocationResource {

    private static final Logger LOG = LoggerFactory.getLogger(GeoLocationResource.class);

    @Override
    public GeoLocationResponse geoLocate(GeoLocationRequest request) {
        LOG.info("Got request: {}", request);
        GeoLocationResponse response = new GeoLocationResponse(52.01796447569499, 4.707350074822482, 1.0);
        LOG.info("Returning mock response: {}", response);
        return response;
    }
}
