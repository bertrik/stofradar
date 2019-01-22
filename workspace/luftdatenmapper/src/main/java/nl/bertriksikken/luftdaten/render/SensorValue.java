package nl.bertriksikken.luftdaten.render;

public final class SensorValue {

    private final double x;
    private final double y;
    private final double value;

    public SensorValue(double x, double y, double value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getValue() {
        return value;
    }

}
