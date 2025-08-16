package nl.bertriksikken.stofradar.restapi;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class PmResultTest {

    @Test
    public void testSerialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // ordinary number
        assertEquals("{\"pm2.5\":12.46,\"sensors\":[]}", mapper.writeValueAsString(new AirResult(12.45678)));
        // NaN
        assertEquals("{\"sensors\":[]}", mapper.writeValueAsString(new AirResult(Double.NaN)));
    }

}
