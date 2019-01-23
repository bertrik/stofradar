package nl.bertriksikken.luftdaten.render;

import java.util.List;

import nl.bertriksikken.luftdaten.config.Coord;
import nl.bertriksikken.luftdaten.config.RenderJob;

/**
 * Interpolates dust values on a (lat,lon) grid in between sensors.  
 */
public final class Interpolator {

    private final double KM_PER_DEGREE_LAT = 40000.0 / 360.0;
	
	/**
     * Interpolates values into a grid.
     * 
     * @param sensorValues the input data
     * @param topLeft the top-left coordinate
     * @param size the size of the coordinates
     * @param w the output width
     * @param h the output height
     * @return grid of double values with the interpolated data
     */
    public double[][] interpolate(List<SensorValue> sensorValues, RenderJob job, int w, int h, double maxDistance) {
        // calculate km per degree
        Coord center = new Coord((job.getTopLeft().getX() + job.getBottomRight().getX()) / 2,
                (job.getTopLeft().getY() + job.getBottomRight().getY()) / 2);
        double[] aspect = new double[] { KM_PER_DEGREE_LAT * Math.cos(Math.toRadians(center.getY())),
                KM_PER_DEGREE_LAT };

        Coord size = new Coord(job.getBottomRight().getX() - job.getTopLeft().getX(),
                job.getTopLeft().getY() - job.getBottomRight().getY());

        // interpolate
        double[][] field = new double[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                double lon = job.getTopLeft().getX() + (0.5 + x) * size.getX() / w;
                double lat = job.getTopLeft().getY() - (0.5 + y) * size.getY() / h;
				Coord pixel = new Coord(lon, lat);
                double v = calculatePixel(sensorValues, pixel, aspect, maxDistance);
				field[x][y] = v;
			}
		}
		return field;
	}

    private double calculatePixel(List<SensorValue> sensorValues, Coord pixel, double[] aspect, double maxDistance) {
		double weightSum = 0.0;
		double valueSum = 0.0;
        double closest = Double.MAX_VALUE;
        for (SensorValue dp : sensorValues) {
            Coord c1 = new Coord(dp.getX(), dp.getY());
			double d2 = distance2(aspect, pixel, c1);
            if (d2 < closest) {
                closest = d2;
            }
            double w = 1.0 / d2;
            double v = dp.getValue();
            valueSum += (v * w);
            weightSum += w;
		}
        return (closest < (maxDistance * maxDistance)) ? valueSum / weightSum : Double.NaN;
	}
	
	/**
	 * Calculates a measure of the distance between two coordinates.
	 * 
	 * @param aspect the x/y aspect ratio
	 * @param c1 the first coordinate
	 * @param c2 the second coordinate
	 * @return distance-squared
	 */
    private double distance2(double[] aspect, Coord c1, Coord c2) {
        double dx = aspect[0] * (c1.getX() - c2.getX());
        double dy = aspect[1] * (c1.getY() - c2.getY());
		return (dx * dx) + (dy * dy);
	}
	
}
