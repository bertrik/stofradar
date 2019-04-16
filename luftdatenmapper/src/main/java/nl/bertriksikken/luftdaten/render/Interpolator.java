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
     * @param job the render job
     * @param w the output width
     * @param h the output height
     * @param shader the shader to calculate each pixel
     * @return grid of double values with the interpolated data
     */
    public double[][] interpolate(List<SensorValue> sensorValues, RenderJob job, int w, int h, IShader shader) {
        Coord size = new Coord(job.getEast() - job.getWest(), job.getNorth() - job.getSouth());

        // interpolate
        double[][] field = new double[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                double lon = job.getWest() + (0.5 + x) * size.getX() / w;
                double lat = job.getNorth() - (0.5 + y) * size.getY() / h;
				Coord pixel = new Coord(lon, lat);
                double v = shader.calculatePixel(sensorValues, pixel);
				field[x][y] = v;
			}
		}
		return field;
	}

}
