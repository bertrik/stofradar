package nl.bertriksikken.stofradar.geolocation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public final class GeoLocationRequestTest {

    @Test
    public void testSerialize() throws JsonProcessingException {
        GeoLocationRequest request = new GeoLocationRequest(true);
        request.addAccessPoint("11:22:33:44:55:66", -77);
        request.addAccessPoint("aa:bb:cc:dd:ee:ff", -88);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);
        Assertions.assertEquals("{\"considerIp\":true,\"wifiAccessPoints\":[{\"macAddress\":\"11:22:33:44:55:66\"," +
                "\"signalStrength\":-77},{\"macAddress\":\"aa:bb:cc:dd:ee:ff\",\"signalStrength\":-88}]}", json);
    }

    @Test
    public void testDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        URL url = getClass().getClassLoader().getResource("GeoLocationRequest.json");
        GeoLocationRequest request = mapper.readValue(url, GeoLocationRequest.class);
        Assertions.assertTrue(request.considerIp());
        Assertions.assertEquals(3, request.accessPoints().size());
    }
}
