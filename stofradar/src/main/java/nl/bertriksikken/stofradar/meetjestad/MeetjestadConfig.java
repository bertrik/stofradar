package nl.bertriksikken.stofradar.meetjestad;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class MeetjestadConfig {

    @JsonProperty("url")
    private String url = "https://meetjestad.net";

    @JsonProperty("timeout")
    private long timeoutSec = 30;

    /**
     * @return the base API URL
     */
    public String getUrl() {
        return url;
    }

    public long getTimeoutSec() {
        return timeoutSec;
    }

}
