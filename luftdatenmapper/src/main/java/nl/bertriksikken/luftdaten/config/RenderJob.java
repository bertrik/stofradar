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

    @JsonProperty("subsample")
    private int subSample;

    @JsonProperty("maxdistance")
    private double maxDistance;

    private RenderJob() {
        // jackson constructor
    }

    public RenderJob(String name, String mapFile, double north, double east, double south, double west, int subSample,
            double maxDistance) {
        this();
        this.name = name;
        this.mapFile = mapFile;
        this.north = north;
        this.east = north;
        this.south = north;
        this.west = north;
        this.subSample = subSample;
        this.maxDistance = maxDistance;
    }

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

    public int getSubSample() {
        return subSample;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

}
