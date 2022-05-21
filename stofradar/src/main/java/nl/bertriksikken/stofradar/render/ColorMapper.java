package nl.bertriksikken.stofradar.render;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps an intensity value to an RGB colour.
 */
public final class ColorMapper {

    private final ColorPoint[] range;
    private final Map<Integer, int[]> cache = new HashMap<>();

    /**
     * Constructor.
     * 
     * @param range the color range
     */
    public ColorMapper(ColorPoint[] range) {
        this.range = range.clone();
    }

    public int[] getColour(double value) {
        int intVal = (int) Math.round(value);
        int[] color = cache.get(intVal);
        if (color == null) {
            color = calculateColour(intVal);
            cache.put(intVal, color);
        }
        return color;
    }

    private int[] calculateColour(double v) {
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
