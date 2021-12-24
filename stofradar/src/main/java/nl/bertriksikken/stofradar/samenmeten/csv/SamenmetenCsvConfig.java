package nl.bertriksikken.stofradar.samenmeten.csv;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class SamenmetenCsvConfig {
    
    @JsonProperty("url")
    private String url = "https://samenmeten.rivm.nl";
    
    @JsonProperty("timeout")
    private long timeoutSec = 30;

    public String getUrl() {
        return url;
    }

    public Duration getTimeout() {
        return Duration.ofSeconds(timeoutSec);
    }

}
