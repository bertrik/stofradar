package nl.bertriksikken.luftdaten.api.dto;

import java.util.ArrayList;

public final class SensorDataValues extends ArrayList<DataValue> {

	/**
	 * Mandatory serial id.
	 */
	private static final long serialVersionUID = 1L;
	
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
