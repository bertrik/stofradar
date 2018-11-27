package nl.bertriksikken.luftdaten;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class RenderJob {

    @JsonProperty("name")
    private String name;

    @JsonProperty("mapfile")
    private String mapFile;

    @JsonProperty("topleft")
    private Coord topleft;

    @JsonProperty("botright")
    private Coord bottomRight;

    @JsonProperty("subsample")
    private int subSample;

    private RenderJob() {
        // jackson constructor
    }

    public RenderJob(String name, String mapFile, Coord topleft, Coord bottomRight, int subSample) {
        this();
        this.name = name;
        this.mapFile = mapFile;
        this.topleft = topleft;
        this.bottomRight = bottomRight;
        this.subSample = subSample;
    }

    public String getName() {
        return name;
    }

    public String getMapFile() {
        return mapFile;
    }

    public Coord getTopLeft() {
        return topleft;
    }

    public Coord getBottomRight() {
        return bottomRight;
    }

    public int getSubSample() {
        return subSample;
    }

}
