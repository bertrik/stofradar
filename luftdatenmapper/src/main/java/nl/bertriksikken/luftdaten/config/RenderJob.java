package nl.bertriksikken.luftdaten.config;

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
    @JsonProperty("minRadius")
    private final double minRadius;
    
    // the radius of the semi-transparent part around a measurement station (km)
    @JsonProperty("maxDistance")
    private final double maxDistance;

    // jackson constructor
    @SuppressWarnings("unused")
    private RenderJob() {
        this("name", "background.png", 53.560406, 3.359403, 50.750938, 7.227496, 1.0, 10.0);
    }

    RenderJob(String name, String map, double north, double west, double south, double east, double minRadius,
            double maxDistance) {
        this.name = name;
        this.map = map;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
        this.minRadius = minRadius;
        this.maxDistance = maxDistance;
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
    
    public double getMinRadius() {
        return minRadius;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

}
