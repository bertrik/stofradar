package nl.bertriksikken.stofradar.restapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class AirRestApiConfig {

    @JsonProperty("port")
    private int port = 9000;

    @JsonProperty("maxDistance")
    private double maxDistance = 1.0;

    public int getPort() {
        return port;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

}
