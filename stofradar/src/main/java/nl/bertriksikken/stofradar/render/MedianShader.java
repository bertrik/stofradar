package nl.bertriksikken.stofradar.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.bertriksikken.stofradar.config.RenderJob;

public final class MedianShader implements IShader {

    private static final double KM_PER_DEGREE_LAT = 40075.0 / 360.0;

    private final double innerRadiusSquared;
    private final double maxDistanceSquared;
    private final ColorMapper mapper;
    private final double[] aspect;

    public MedianShader(RenderJob job, ColorMapper mapper) {
        this.innerRadiusSquared = Math.pow(job.getInnerRadius(), 2.0);
        this.maxDistanceSquared = Math.pow(job.getOuterRadius(), 2.0);
        this.mapper = mapper;
        
        // calculate km per degree
        Coord center = new Coord((job.getWest() + job.getEast()) / 2, (job.getNorth() + job.getSouth()) / 2);
        this.aspect = new double[] { KM_PER_DEGREE_LAT * Math.cos(Math.toRadians(center.getY())), KM_PER_DEGREE_LAT };
    }

    
    @Override
    public int[] calculatePixel(List<SensorValue> sensorValues, Coord coordinate) {
        
        // find sensors within radius
        double closestDistSquared = Double.MAX_VALUE;
        double closestDistValue = 0.0;
        List<SensorValue> near = new ArrayList<>();
        for (SensorValue dp : sensorValues) {
            Coord c1 = new Coord(dp.x, dp.y);
            double d2 = distanceSquared(aspect, coordinate, c1);
            if (d2 < maxDistanceSquared) {
                near.add(dp);
                if (d2 < closestDistSquared) {
                    closestDistSquared = d2;
                    closestDistValue = dp.value;
                }
            }
        }
        if (near.isEmpty()) {
            return new int[] {0, 0, 0, 0};
        }
        if (closestDistSquared < innerRadiusSquared) {
            // inside inner radius: fully opaque disc 
            int[] colour = mapper.getColour(closestDistValue).clone();
            colour[3] = 255;
            return colour;
        }
        // calculate median
        double value = median(near);
        return mapper.getColour(value);
    }
    
    private double median(List<SensorValue> values) {
        int len = values.size();
        Collections.sort(values, (v1, v2) -> Double.compare(v1.value, v2.value));
        double left = values.get((len - 1) / 2).value;
        double right = values.get(len / 2).value;
        return (left + right) / 2;
    }
    

    private double distanceSquared(double[] aspect, Coord c1, Coord c2) {
        double dx = aspect[0] * (c1.getX() - c2.getX());
        double dy = aspect[1] * (c1.getY() - c2.getY());
        return (dx * dx) + (dy * dy);
    }

}
