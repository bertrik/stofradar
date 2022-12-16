package nl.bertriksikken.stofradar.render;

import java.util.List;

import nl.bertriksikken.stofradar.config.RenderJob;

public final class InverseDistanceWeightShader implements IShader {

    private static final double KM_PER_DEGREE_LAT = 40075.0 / 360.0;

    private final double innerRadius;
    private final double outerRadius;
    private final ColorMapper mapper;
    private final double[] aspect;

    /**
     * Constructor.
     * 
     * @param job the render job
     */
    public InverseDistanceWeightShader(RenderJob job, ColorMapper mapper) {
        this.innerRadius = job.getInnerRadius();
        this.outerRadius = job.getOuterRadius();
        this.mapper = mapper;

        // calculate km per degree
        Coord center = new Coord((job.getWest() + job.getEast()) / 2, (job.getNorth() + job.getSouth()) / 2);
        this.aspect = new double[] { KM_PER_DEGREE_LAT * Math.cos(Math.toRadians(center.y)), KM_PER_DEGREE_LAT };
    }

    @Override
    public int[] calculatePixel(List<SensorValue> sensorValues, Coord coordinate) {
        double weightSum = 0.0;
        double valueSum = 0.0;
        double closestDistSquared = Double.MAX_VALUE;
        double closestDistValue = 0.0;
        for (SensorValue dp : sensorValues) {
            Coord c1 = new Coord(dp.x, dp.y);
            double d2 = distanceSquared(aspect, coordinate, c1);
            double w = 1.0 / d2;
            double v = dp.value;
            valueSum += (v * w);
            weightSum += w;
            if (d2 < closestDistSquared) {
                closestDistSquared = d2;
                closestDistValue = v;
            }
        }
        double closest = Math.sqrt(closestDistSquared);
        double weighted = valueSum / weightSum;

        int[] colour;
        if (closest < innerRadius) {
            // inside inner radius: fully opaque disc
            colour = mapper.getColour(closestDistValue).clone();
            colour[3] = 255;
        } else if (closest < outerRadius) {
            // between inner and outer radius: semi-transparent weighted sum
            colour = mapper.getColour(weighted);
        } else {
            // else fully transparent
            colour = new int[] { 0, 0, 0, 0 };
        }
        return colour;
    }

    /**
     * Calculates a measure of the distance between two coordinates.
     * 
     * @param aspect the x/y aspect ratio
     * @param c1     the first coordinate
     * @param c2     the second coordinate
     * @return distance-squared
     */
    private double distanceSquared(double[] aspect, Coord c1, Coord c2) {
        double dx = aspect[0] * (c1.x - c2.x);
        double dy = aspect[1] * (c1.y - c2.y);
        return (dx * dx) + (dy * dy);
    }

}
