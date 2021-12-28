package nl.bertriksikken.stofradar.senscom;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.NONE)
public final class SensComConfig {

    @JsonProperty("url")
    private String url = "https://api.sensor.community";
    
    @JsonProperty("timeout")
	private long timeoutSec = 20;

    @JsonProperty("blacklist")
    private List<String> blacklist = Arrays.asList("11697");

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
    
    /**
     * @return the list of blacklisted nodes by number
     */
    public List<String> getBlacklist() {
    	return Collections.unmodifiableList(blacklist);
    }

}
