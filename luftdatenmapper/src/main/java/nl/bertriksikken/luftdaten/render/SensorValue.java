package nl.bertriksikken.luftdaten.render;

public final class SensorValue {

	private final int id;
    private final double x;
    private final double y;
    private final double value;

    public SensorValue(int id, double x, double y, double value) {
        this.id = id;
    	this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getId() {
    	return id;
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
