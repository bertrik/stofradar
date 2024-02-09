package nl.bertriksikken.stofradar.config;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.stofradar.render.EDataSource;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
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

    @JsonProperty("minimumScore")
    private final int minimumScore;

    @JsonProperty("source")
    private final String source;

    // jackson constructor
    @SuppressWarnings("unused")
    private RenderJob() {
        this("name", "background.png", 53.560406, 3.359403, 50.750938, 7.227496, 1.0, 10.0, 5, "*");
    }

    RenderJob(String name, String map, double north, double west, double south, double east, double innerRadius,
            double outerRadius, int minimumScore, String source) {
        this.name = name;
        this.map = map;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.minimumScore = minimumScore;
        this.source = source;
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

    @Override
    public String toString() {
        return name;
    }

    public int getMinimumScore() {
        return minimumScore;
    }

    public Set<EDataSource> getSources() {
        if (source.equals("*")) {
            return Stream.of(EDataSource.values()).collect(Collectors.toSet());
        }
        return Stream.of(source.split(",", -1)).map(EDataSource::fromName).filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

}
