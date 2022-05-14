package nl.bertriksikken.stofradar.meetjestad;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeetjestadConfig {

    @JsonProperty("url")
    private String url = "https://meetjestad.net";

    @JsonProperty("timeout")
    private long timeoutSec = 20;

    /**
     * @return the base sensor.community API URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the sensor.community timeout
     */
    public long getTimeoutSec() {
        return timeoutSec;
    }

}
