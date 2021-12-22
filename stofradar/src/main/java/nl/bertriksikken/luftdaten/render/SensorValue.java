package nl.bertriksikken.luftdaten.render;

import java.time.Instant;

public final class SensorValue {

	public final int id;
	public final double x;
	public final double y;
	public final double value;
	public final Instant time;
	
	// jackson constructor
	@SuppressWarnings("unused")
    private SensorValue() {
	    this(0, 0.0, 0.0, 0.0, Instant.now());
	}

    public SensorValue(int id, double x, double y, double value, Instant time) {
        this.id = id;
    	this.x = x;
        this.y = y;
        this.value = value;
        this.time = time;
    }

}
