package nl.bertriksikken.stofradar.render;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

final class Coord {

    @JsonProperty("lon")
    public final double x;
    @JsonProperty("lat")
    public final double y;

    // jackson constructor
    @SuppressWarnings("unused")
    private Coord() {
        x = Double.NaN;
        y = Double.NaN;
    }

    Coord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%f,%f}", x, y);
    }

}
