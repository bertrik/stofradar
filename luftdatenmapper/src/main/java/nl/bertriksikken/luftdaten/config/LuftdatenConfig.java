package nl.bertriksikken.luftdaten.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class LuftdatenConfig {

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
    	return blacklist;
    }

}
