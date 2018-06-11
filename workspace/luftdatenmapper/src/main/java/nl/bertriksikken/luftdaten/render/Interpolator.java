package nl.bertriksikken.luftdaten.render;

import nl.bertriksikken.luftdaten.Coord;
import nl.bertriksikken.luftdaten.api.dto.DataPoint;
import nl.bertriksikken.luftdaten.api.dto.DataPoints;
import nl.bertriksikken.luftdaten.api.dto.DataValue;
import nl.bertriksikken.luftdaten.api.dto.Location;

/**
 * Interpolates dust values on a (lat,lon) grid in between sensors.  
 */
public final class Interpolator {
	
	/**
	 * Interpolates values into a grid.
	 * 
	 * @param dataPoints the input data
	 * @param topLeft the top-left coordinate
	 * @param size the size of the coordinates
	 * @param w the output width
	 * @param h the output height
	 * @return grid of double values with the interpolated data
	 */
	public double[][] interpolate(DataPoints dataPoints, Coord topLeft, Coord size, int w, int h) {
		double aspect = Math.cos(topLeft.getY() * Math.PI / 180.0);
		double[][] field = new double[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				double lon = topLeft.getX() + x * size.getX() / w;
				double lat = topLeft.getY() - y * size.getY() / h;
				Coord pixel = new Coord(lon, lat);
				double v = calculatePixel(dataPoints, pixel, "P1", aspect);
				field[x][y] = v;
			}
		}
		return field;
	}

	private double calculatePixel(DataPoints dataPoints, Coord pixel, String valueId, double aspect) {
		double weightSum = 0.0;
		double valueSum = 0.0;
		for (DataPoint dp : dataPoints) {
			Location l = dp.getLocation();
			Coord c1 = new Coord(l.getLongitude(), l.getLatitude());
			double d2 = distance2(aspect, pixel, c1);
            double w = 1.0 / d2;
			DataValue value = dp.getSensorDataValues().getDataValue(valueId);
			if (value != null) {
				double v = value.getValue();
				valueSum += (v * w);
				weightSum += w;
			}
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
