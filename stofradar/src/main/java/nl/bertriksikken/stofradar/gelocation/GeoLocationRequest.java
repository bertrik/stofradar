package nl.bertriksikken.stofradar.gelocation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A minimal geolocation request, according to
 * <a href="https://ichnaea.readthedocs.io/en/latest/api/geolocate.html">Mozilla location service API</a> and
 * <a href="https://developers.google.com/maps/documentation/geolocation/requests-geolocation">Google Maps platform</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoLocationRequest(@JsonProperty("considerIp") boolean considerIp,
                                 @JsonProperty("wifiAccessPoints") List<WifiAccessPoint> accessPoints) {

    public GeoLocationRequest {
        accessPoints = Objects.requireNonNullElse(accessPoints, new ArrayList<>());
    }

    public GeoLocationRequest(boolean considerIp) {
        this(considerIp, null);
    }

    public void addAccessPoint(String mac, int strength) {
        accessPoints.add(new WifiAccessPoint(mac.toLowerCase(Locale.ROOT), strength));
    }

    public record WifiAccessPoint(@JsonProperty("macAddress") String mac,
                                  @JsonProperty("signalStrength") int signal) {
        // google maps requires colon-separated mac address
    }
}
