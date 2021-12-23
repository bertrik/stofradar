package nl.bertriksikken.stofradar.render;

/**
 * A color point in a color range.
 */
public final class ColorPoint {

    private final double value;
    private final int[] rgb;

    /**
     * Constructor.
     * 
     * @param value
     *            the measurement value
     * @param rgb
     *            the associated rgb value
     */
    public ColorPoint(double value, int[] rgb) {
        this.value = value;
        this.rgb = rgb.clone();
    }

    public double getValue() {
        return value;
    }

    public int[] getRgb() {
        return rgb.clone();
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
