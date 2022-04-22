package nl.bertriksikken.stofradar.restapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class PmResult {
    
    @JsonProperty("pm2.5")
    private double pm2_5;
    
    // jackson constructor
    PmResult() {
        this(0.0);
    }
    
    PmResult(double pm2_5) {
        this.pm2_5 = pm2_5;
    }
    
}
