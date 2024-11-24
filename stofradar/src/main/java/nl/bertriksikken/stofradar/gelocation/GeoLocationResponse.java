package nl.bertriksikken.stofradar.gelocation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * A minimal WiFi geolocation response, according to
 * <a href="https://ichnaea.readthedocs.io/en/latest/api/geolocate.html">docs</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public record GeoLocationResponse(@JsonProperty("location") Location location,
                                  @JsonProperty("accuracy") Double accuracy) {

    public GeoLocationResponse {
        location = Objects.requireNonNullElse(location, new Location(null, null));
        accuracy = Objects.requireNonNullElse(accuracy, Double.NaN);
    }

    public GeoLocationResponse(double latitude, double longitude, double accuracy) {
        this(new Location(latitude, longitude), accuracy);
    }

    public static GeoLocationResponse invalid() {
        return new GeoLocationResponse(null, null);
    }

    public boolean isValid() {
        return Double.isFinite(accuracy) && location().isValid();
    }

    public record Location(@JsonProperty("lat") Double latitude, @JsonProperty("lng") Double longitude) {
        public Location {
            latitude = Objects.requireNonNullElse(latitude, Double.NaN);
            longitude = Objects.requireNonNullElse(longitude, Double.NaN);
        }

        boolean isValid() {
            return Double.isFinite(latitude) && Double.isFinite(longitude);
        }
    }
}
