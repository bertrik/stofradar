package nl.bertriksikken.stofradar.senscom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class DataPointTest {

    @Test
    public void testSerialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        URL url = getClass().getClassLoader().getResource("datapoint.json");
        DataPoint dp = mapper.readValue(url, DataPoint.class);
        assertNotNull(dp);
        Instant timestamp = dp.getTimestamp();
        assertNotNull(timestamp);
    }
    
}
