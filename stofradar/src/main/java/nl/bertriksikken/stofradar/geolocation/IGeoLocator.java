package nl.bertriksikken.stofradar.geolocation;

import java.io.IOException;

public interface IGeoLocator {
    GeoLocationResponse geoLocate(GeoLocationRequest request) throws IOException;
}
