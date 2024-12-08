package nl.bertriksikken.stofradar.restapi;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class PmResultTest {

    @Test
    public void testSerialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // ordinary number
        Assertions.assertEquals("{\"pm2.5\":12.46,\"sensors\":[]}", mapper.writeValueAsString(new AirResult(12.45678)));
        // NaN
        Assertions.assertEquals("{\"sensors\":[]}", mapper.writeValueAsString(new AirResult(Double.NaN)));
    }

}
