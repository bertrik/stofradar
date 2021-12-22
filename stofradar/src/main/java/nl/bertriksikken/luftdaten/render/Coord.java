package nl.bertriksikken.luftdaten.render;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

final class Coord {

    @JsonProperty("lon")
    private double x;
    @JsonProperty("lat")
    private double y;

    private Coord() {
        // jackson constructor
    }

    Coord(double x, double y) {
        this();
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{x=%f,y=%f}", x, y);
    }

}
