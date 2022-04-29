package nl.bertriksikken.stofradar.restapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class AirRestApiConfig {

    @JsonProperty("port")
    private int port = 9000;

    @JsonProperty("path")
    private String path = "";

    @JsonProperty("maxDistance")
    private double maxDistance = 10;

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

}
