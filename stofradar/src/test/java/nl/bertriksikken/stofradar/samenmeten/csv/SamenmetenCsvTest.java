package nl.bertriksikken.stofradar.samenmeten.csv;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class SamenmetenCsvTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(SamenmetenCsvTest.class);

    @Test
    public void testDeserialize() throws IOException, URISyntaxException {
        URI uri = getClass().getClassLoader().getResource("lucht.txt").toURI();
        String lucht = Files.readString(Paths.get(uri), StandardCharsets.US_ASCII);
        SamenmetenCsv csv = SamenmetenCsv.parse(lucht);
        LOG.info("csv = {}", csv);
    }
    
}
