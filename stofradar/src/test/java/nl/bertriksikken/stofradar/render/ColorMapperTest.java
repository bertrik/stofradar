package nl.bertriksikken.stofradar.render;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ColorMapperTest {

    private static final ColorPoint[] RANGE_PM2_5 = new ColorPoint[]{
            // good
            new ColorPoint(0, new int[]{0, 100, 255, 0x00}), new ColorPoint(10, new int[]{0, 175, 255, 0x60}),
            new ColorPoint(15, new int[]{150, 200, 255, 0xC0}),
            // not so good
            new ColorPoint(20, new int[]{255, 255, 200, 0xC0}),
            new ColorPoint(30, new int[]{255, 255, 150, 0xC0}), new ColorPoint(40, new int[]{255, 255, 0, 0xC0}),
            // insufficient
            new ColorPoint(50, new int[]{255, 200, 0, 0xC0}), new ColorPoint(70, new int[]{255, 150, 0, 0xC0}),
            // bad
            new ColorPoint(90, new int[]{255, 75, 0, 0xC0}), new ColorPoint(100, new int[]{255, 25, 0, 0xC0}),
            // very bad
            new ColorPoint(140, new int[]{164, 58, 217, 0xC0})};

    @Test
    public void testCache() {
        ColorMapper mapper = new ColorMapper(RANGE_PM2_5);

        int[] zero = mapper.getColour(0);
        Assertions.assertEquals(0, zero[3]);

        int[] bad = mapper.getColour(90);
        Assertions.assertArrayEquals(new int[]{255, 75, 0, 0xC0}, bad);
    }

}
