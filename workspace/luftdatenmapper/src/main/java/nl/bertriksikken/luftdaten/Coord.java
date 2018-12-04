package nl.bertriksikken.luftdaten;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Coord {

    @JsonProperty("lon")
    private double x;
    @JsonProperty("lat")
    private double y;

    private Coord() {
        // jackson constructor
    }

    public Coord(double x, double y) {
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
        return String.format(Locale.US, "{x=%f,y=%f}", x, y);
    }

}
