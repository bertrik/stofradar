package nl.bertriksikken.luftdaten.render;

import java.awt.image.WritableRaster;
import java.util.List;

import nl.bertriksikken.luftdaten.config.Coord;
import nl.bertriksikken.luftdaten.config.RenderJob;

/**
 * Interpolates dust values on a (lat,lon) grid in between sensors.  
 */
public final class Interpolator {

	private final RenderJob job;
	private final IShader shader;
	private final int width;
	private final int height;

	public Interpolator(RenderJob job, IShader shader, int width, int height) {
		this.job = job;
		this.shader = shader;
		this.width = width;
		this.height = height;
	}

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
    public void interpolate(List<SensorValue> sensorValues, WritableRaster raster) {
        Coord size = new Coord(job.getEast() - job.getWest(), job.getNorth() - job.getSouth());

        // interpolate
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double lon = job.getWest() + (0.5 + x) * size.getX() / width;
                double lat = job.getNorth() - (0.5 + y) * size.getY() / height;
				Coord pixel = new Coord(lon, lat);
                int[] colour = shader.calculatePixel(sensorValues, pixel);
				raster.setPixel(x, y, colour);
			}
		}
	}

}
