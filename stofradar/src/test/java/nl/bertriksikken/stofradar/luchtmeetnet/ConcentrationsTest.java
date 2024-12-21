package nl.bertriksikken.stofradar.luchtmeetnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public final class ConcentrationsTest {

    @Test
    public void testDeserialize() throws IOException {
        URL url = getClass().getClassLoader().getResource("concentrations.json");
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        Concentrations concentrations = mapper.readValue(url, Concentrations.class);
        Assertions.assertNotNull(concentrations);
    }
}
