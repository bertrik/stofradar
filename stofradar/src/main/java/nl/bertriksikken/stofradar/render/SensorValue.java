package nl.bertriksikken.stofradar.render;

import java.time.Instant;

public final class SensorValue {

    public final String id;
    public final double x;
    public final double y;
    public final double value;
    public final Instant time;
    public int plausibility;

    // jackson constructor
    @SuppressWarnings("unused")
    private SensorValue() {
        this("", 0.0, 0.0, 0.0, Instant.now());
    }

    public SensorValue(String id, double x, double y, double value, Instant time) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.value = value;
        this.time = time;
        this.plausibility = -1;
    }

    public void setPlausibility(int plausibility) {
        this.plausibility = plausibility;
    }
}
