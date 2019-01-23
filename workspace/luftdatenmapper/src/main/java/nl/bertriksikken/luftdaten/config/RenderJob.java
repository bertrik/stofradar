package nl.bertriksikken.luftdaten.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "name", "mapfile", "topleft", "botright", "subsample" })
public final class RenderJob {

    @JsonProperty("name")
    private String name;

    @JsonProperty("mapfile")
    private String mapFile;

    @JsonProperty("topleft")
    private Coord topLeft;

    @JsonProperty("botright")
    private Coord bottomRight;

    @JsonProperty("subsample")
    private int subSample;

    @JsonProperty("maxdistance")
    private double maxDistance;

    private RenderJob() {
        // jackson constructor
    }

    public RenderJob(String name, String mapFile, Coord topleft, Coord bottomRight, int subSample, double maxDistance) {
        this();
        this.name = name;
        this.mapFile = mapFile;
        this.topLeft = topleft;
        this.bottomRight = bottomRight;
        this.subSample = subSample;
        this.maxDistance = maxDistance;
    }

    public String getName() {
        return name;
    }

    public String getMapFile() {
        return mapFile;
    }

    public Coord getTopLeft() {
        return topLeft;
    }

    public Coord getBottomRight() {
        return bottomRight;
    }

    public int getSubSample() {
        return subSample;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

}
