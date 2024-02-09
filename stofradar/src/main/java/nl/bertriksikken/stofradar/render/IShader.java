package nl.bertriksikken.stofradar.render;

import java.util.List;

public interface IShader {

    /**
     * Calculates the value at some specific coordinate.
     * 
     * @param sensorValues the sensor data
     * @param coordinate the coordinate
     * @return the value, RGBA array
     */
    int[] calculatePixel(List<SensorValue> sensorValues, Coord coordinate);

}
