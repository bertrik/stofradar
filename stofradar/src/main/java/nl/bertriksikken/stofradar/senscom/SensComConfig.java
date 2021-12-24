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
    private List<Integer> blacklist = Arrays.asList(11697);

	/**
     * @return the base luftdaten.info URL
     */
    public String getUrl() {
    	return url;
    }
    
    /**
     * @return the luftdaten.info timeout
     */
    public long getTimeoutSec() {
    	return timeoutSec;
    }
    
    /**
     * @return the list of blacklisted nodes by number
     */
    public List<Integer> getBlacklist() {
    	return Collections.unmodifiableList(blacklist);
    }

}
