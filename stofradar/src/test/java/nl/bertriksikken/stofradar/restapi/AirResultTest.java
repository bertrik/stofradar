package nl.bertriksikken.stofradar.restapi;

import java.time.Instant;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.stofradar.render.EDataSource;
import nl.bertriksikken.stofradar.render.SensorValue;

public final class AirResultTest {

    @Test
    public void test() throws JsonProcessingException {
        AirResult result = new AirResult(1.2345678);
        result.addSensor(new SensorValue(EDataSource.SENSOR_COMMUNITY, "id", 0.0, 0.0, 12.456776890, Instant.now()));
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        System.out.println(s);
    }
}
