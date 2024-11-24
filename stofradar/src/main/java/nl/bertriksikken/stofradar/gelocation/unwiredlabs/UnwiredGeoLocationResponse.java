package nl.bertriksikken.stofradar.gelocation.unwiredlabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UnwiredGeoLocationResponse(@JsonProperty("status") String status, @JsonProperty("message") String message,
                                         @JsonProperty("balance") int balance,
                                         @JsonProperty("lat") Double latitude, @JsonProperty("lon") Double longitude,
                                         @JsonProperty("accuracy") Double accuracy,
                                         @JsonProperty("address") String address) {

    public UnwiredGeoLocationResponse {
        status = Objects.requireNonNullElse(status, "");
        message = Objects.requireNonNullElse(message, "");
        latitude = Objects.requireNonNullElse(latitude, Double.NaN);
        longitude = Objects.requireNonNullElse(longitude, Double.NaN);
        accuracy = Objects.requireNonNullElse(accuracy, Double.NaN);
        address = Objects.requireNonNullElse(address, "");
    }
}
