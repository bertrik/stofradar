package nl.bertriksikken.luftdaten;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.luftdaten.LuftdatenMapper;
import nl.bertriksikken.luftdaten.api.dto.DataPoints;

/**
 * @author bertrik
 *
 */
public final class LuftdatenMapperTest {
	
	@Test
	public void test2() throws IOException {
		// read file
//		final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("data.dust.min.json");
//		final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("201806032249_data.dust.min.json");
		final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("201806042257_data.dust.min.json");
		
		ObjectMapper mapper = new ObjectMapper();
		DataPoints dataPoints = mapper.readValue(is, DataPoints.class);

		// create CSV
		LuftdatenMapper luftdatenMapper = new LuftdatenMapper();
		luftdatenMapper.createHeatMap(dataPoints, new FileWriter(new File("heatmap3.csv")));
	}

	
}
