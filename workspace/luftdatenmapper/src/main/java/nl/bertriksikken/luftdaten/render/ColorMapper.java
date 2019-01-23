package nl.bertriksikken.luftdaten.render;

import java.awt.image.WritableRaster;

/**
 * Maps an image consisting of scalar values to an image with RGB values.
 */
public final class ColorMapper {

    private static final int[] NO_DATA_COLOUR = { 0x80, 0x80, 0x80, 0x80 };

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

    /**
     * Performs the actual mapping
     * 
     * @param values
     *            the scalar values
     * @param raster
     *            the bitmap raster to write to
     */
    public void map(double[][] values, WritableRaster raster) {
        for (int x = 0; x < values.length; x++) {
            for (int y = 0; y < values[0].length; y++) {
                Double v = values[x][y];
                int[] colour = v.isNaN() ? NO_DATA_COLOUR : getColour(range, v);
                raster.setPixel(x, y, colour);
            }
        }
    }

    private int[] getColour(ColorPoint[] range, double v) {
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
