package nl.bertriksikken.stofradar.render;

/**
 * Maps an image consisting of scalar values to an image with RGB values.
 */
public final class ColorMapper {

    private final ColorPoint[] range;

    /**
     * Constructor.
     * 
     * @param range
     *            the color range
     */
    public ColorMapper(ColorPoint[] range) {
        this.range = range.clone();
    }

    public int[] getColour(double v) {
        ColorPoint cp = range[0];
        for (int i = 1; i < range.length; i++) {
            ColorPoint next = range[i];
            double dv = (v - cp.getValue()) / (next.getValue() - cp.getValue());
            if ((dv >= 0.0) && (dv < 1.0)) {
                int[] rgba = new int[cp.getRgb().length];
                for (int j = 0; j < cp.getRgb().length; j++) {
                    int c = (int) Math.round((1.0 - dv) * cp.getRgb()[j] + dv * next.getRgb()[j]);
                    rgba[j] = c;
                }
                return rgba;
            }
            cp = next;
        }

        return range[range.length - 1].getRgb();
    }

}
