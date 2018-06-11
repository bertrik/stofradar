package nl.bertriksikken.luftdaten;

import java.util.Locale;

public final class Coord {

    private double x;
    private double y;

    public Coord(double x, double y) {
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
