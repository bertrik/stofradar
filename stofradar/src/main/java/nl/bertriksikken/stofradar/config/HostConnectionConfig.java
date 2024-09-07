package nl.bertriksikken.stofradar.config;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
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
     * Returns the base sensor.community API URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the sensor.community timeout
     */
    public Duration getTimeout() {
        return Duration.ofSeconds(timeoutSec);
    }

}
