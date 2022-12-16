package nl.bertriksikken.stofradar.senscom;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.NONE)
public final class SensComConfig {

    @JsonProperty("url")
    private String url = "https://api.sensor.community";
    
    @JsonProperty("timeout")
	private long timeoutSec = 30;

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
