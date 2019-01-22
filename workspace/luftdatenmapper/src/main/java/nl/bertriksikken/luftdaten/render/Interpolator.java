package nl.bertriksikken.luftdaten.render;

import java.util.List;

import nl.bertriksikken.luftdaten.config.Coord;
import nl.bertriksikken.luftdaten.config.RenderJob;

/**
 * Interpolates dust values on a (lat,lon) grid in between sensors.  
 */
public final class Interpolator {
	
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
    public double[][] interpolate(List<SensorValue> sensorValues, RenderJob job, int w, int h) {
        double aspect = Math.cos(job.getTopLeft().getY() * Math.PI / 180.0);
        double[][] field = new double[w][h];
        Coord size = new Coord(job.getBottomRight().getX() - job.getTopLeft().getX(),
                job.getTopLeft().getY() - job.getBottomRight().getY());
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                double lon = job.getTopLeft().getX() + (0.5 + x) * size.getX() / w;
                double lat = job.getTopLeft().getY() - (0.5 + y) * size.getY() / h;
				Coord pixel = new Coord(lon, lat);
                double v = calculatePixel(sensorValues, pixel, aspect);
				field[x][y] = v;
			}
		}
		return field;
	}

    private double calculatePixel(List<SensorValue> sensorValues, Coord pixel, double aspect) {
		double weightSum = 0.0;
		double valueSum = 0.0;
        for (SensorValue dp : sensorValues) {
            Coord c1 = new Coord(dp.getX(), dp.getY());
			double d2 = distance2(aspect, pixel, c1);
            double w = 1.0 / d2;
            double v = dp.getValue();
            valueSum += (v * w);
            weightSum += w;
		}
		return valueSum / weightSum;
	}
	
	/**
	 * Calculates a measure of the distance between two coordinates.
	 * 
	 * @param aspect the x/y aspect ratio
	 * @param c1 the first coordinate
	 * @param c2 the second coordinate
	 * @return distance-squared
	 */
	private double distance2(double aspect, Coord c1, Coord c2) {
		double dx = aspect * (c1.getX() - c2.getX());
		double dy = (c1.getY() - c2.getY());
		return (dx * dx) + (dy * dy);
	}
	
}
