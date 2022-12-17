package nl.bertriksikken.stofradar.config;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class HostConnectionConfig {

    @JsonProperty("url")
    private final String url;

    @JsonProperty("timeout")
    private final int timeoutSec;

    // no-arg jackson constructor
    @SuppressWarnings("unused")
    private HostConnectionConfig() {
        url = "";
        timeoutSec = 0;
    }

    public HostConnectionConfig(String url, int timeoutSec) {
        this.url = url;
        this.timeoutSec = timeoutSec;
    }

    /**
     * @return the base sensor.community API URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the sensor.community timeout
     */
    public Duration getTimeout() {
        return Duration.ofSeconds(timeoutSec);
    }

}
