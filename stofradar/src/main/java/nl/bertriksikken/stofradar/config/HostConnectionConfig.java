package nl.bertriksikken.stofradar.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;

public record HostConnectionConfig(@JsonProperty("url") String url, @JsonProperty("timeout") int timeoutSec) {

    /**
     * Returns the sensor.community timeout
     */
    public Duration timeout() {
        return Duration.ofSeconds(timeoutSec);
    }

}
