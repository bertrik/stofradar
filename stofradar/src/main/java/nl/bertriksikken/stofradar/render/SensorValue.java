package nl.bertriksikken.stofradar.render;

import java.time.Instant;

public final class SensorValue {

    public final EDataSource source;
    public final String id;
    public final double x;
    public final double y;
    public final double value;
    public final Instant time;
    private int plausibility;

    // jackson constructor
    @SuppressWarnings("unused")
    private SensorValue() {
        this(null, "", 0.0, 0.0, 0.0, Instant.now());
    }

    public SensorValue(EDataSource source, String id, double x, double y, double value, Instant time) {
        this.source = source;
        this.id = id;
        this.x = x;
        this.y = y;
        this.value = value;
        this.time = time;
        this.plausibility = -1;
    }

    public int getPlausibility() {
        return plausibility;
    }

    public void setPlausibility(int plausibility) {
        this.plausibility = plausibility;
    }
}
