package nl.bertriksikken.luftdaten.render;

public final class SensorValue {

	public final int id;
	public final double x;
	public final double y;
	public final double value;

    public SensorValue(int id, double x, double y, double value) {
        this.id = id;
    	this.x = x;
        this.y = y;
        this.value = value;
    }

}
