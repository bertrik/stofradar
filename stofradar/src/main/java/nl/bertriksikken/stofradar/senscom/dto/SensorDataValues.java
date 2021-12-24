package nl.bertriksikken.stofradar.senscom.dto;

import java.util.ArrayList;
import java.util.List;

public final class SensorDataValues extends ArrayList<DataValue> {

    /**
     * Mandatory serial id.
     */
    private static final long serialVersionUID = 1L;
    
    private SensorDataValues() {
        // mandatory no-arg Jackson constructor
    }
    
    /**
     * Constructor
     * @param values initial data values
     */
    SensorDataValues(List<DataValue> values) {
        this();
        addAll(values);
    }

    /**
     * Extracts a certain data value.
     * 
     * @param name the name of the data value, e.g. "P1".
     * @return the data value
     */
    public DataValue getDataValue(String name) {
        for (DataValue value : this) {
            if (value.getValueType().equals(name)) {
                return value;
            }
        }
        return null;
    }

}
