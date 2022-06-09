package nl.bertriksikken.stofradar.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class RenderJob {

    @JsonProperty("name")
    private final String name;

    @JsonProperty("map")
    private final String map;

    @JsonProperty("north")
    private final double north;

    @JsonProperty("west")
    private final double west;

    @JsonProperty("south")
    private final double south;

    @JsonProperty("east")
    private final double east;

    // the radius of the opaque part around a measurement station (km)
    @JsonProperty("innerRadius")
    private final double innerRadius;

    // the radius of the semi-transparent part around a measurement station (km)
    @JsonProperty("outerRadius")
    private final double outerRadius;

    @JsonProperty("maxAgeMinutes")
    private final int maxAgeMinutes;

    // jackson constructor
    @SuppressWarnings("unused")
    private RenderJob() {
        this("name", "background.png", 53.560406, 3.359403, 50.750938, 7.227496, 1.0, 10.0, 65);
    }

    RenderJob(String name, String map, double north, double west, double south, double east, double innerRadius,
            double outerRadius, int maxAgeMinutes) {
        this.name = name;
        this.map = map;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.maxAgeMinutes = maxAgeMinutes;
    }

    public String getName() {
        return name;
    }

    public String getMapFile() {
        return map;
    }

    public double getNorth() {
        return north;
    }

    public double getWest() {
        return west;
    }

    public double getSouth() {
        return south;
    }

    public double getEast() {
        return east;
    }

    public double getInnerRadius() {
        return innerRadius;
    }

    public double getOuterRadius() {
        return outerRadius;
    }

    public int getMaxAgeMinutes() {
        return maxAgeMinutes;
    }

    @Override
    public String toString() {
        return name;
    }

}
