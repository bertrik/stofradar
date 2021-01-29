package nl.bertriksikken.luftdaten.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "name", "mapfile", "topleft", "botright", "subsample" })
public final class RenderJob {

    @JsonProperty("name")
    private String name;

    @JsonProperty("mapfile")
    private String mapFile;

    @JsonProperty("north")
    private double north;

    @JsonProperty("east")
    private double east;

    @JsonProperty("south")
    private double south;

    @JsonProperty("west")
    private double west;

    // the radius of the semi-transparent part around a measurement station
    @JsonProperty("maxdistance")
    private double maxDistance;
    
    // the radius of the opaque part around a measurement station
    @JsonProperty("minRadius")
    private double minRadius = 0.0;
    
    public String getName() {
        return name;
    }

    public String getMapFile() {
        return mapFile;
    }

    public double getNorth() {
        return north;
    }

    public double getEast() {
        return east;
    }

    public double getSouth() {
        return south;
    }

    public double getWest() {
        return west;
    }

    public double getMaxDistance() {
        return maxDistance;
    }
    
    public double getMinRadius() {
    	return minRadius;
    }

}
