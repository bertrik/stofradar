package nl.bertriksikken.stofradar.senscom.dto;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class DataPointTest {

    @Test
    public void testSerialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        URL url = getClass().getClassLoader().getResource("datapoint.json");
        DataPoint dp = mapper.readValue(url, DataPoint.class);
        Assert.assertNotNull(dp);
        Instant timestamp = dp.getTimestamp();
        Assert.assertNotNull(timestamp);
    }
    
}
