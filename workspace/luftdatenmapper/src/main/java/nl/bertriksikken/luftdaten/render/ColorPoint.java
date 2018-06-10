package nl.bertriksikken.luftdaten.render;

/**
 * A color point in a color range.
 */
public final class ColorPoint {
	
	private final double value;
	private final int[] rgb;

	/**
	 * Constructor.
	 * 
	 * @param value the measurement value
	 * @param rgb the associaetd rgb value
	 */
	public ColorPoint(double value, int[] rgb) {
		this.value = value;
		this.rgb = rgb;
	}

	public double getValue() {
		return value;
	}

	public int[] getRgb() {
		return rgb;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("" + value);
		for (int c : rgb) {
			sb.append(" " + c);
		}
		return sb.toString();
	}
	
}
