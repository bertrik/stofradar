package nl.bertriksikken.stofradar.meetjestad;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import nl.bertriksikken.stofradar.meetjestad.MeetjestadData.MeetjestadDataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Downloads past 15 minutes of meetjestad data.
 */
public final class MeetjestadClientTest {

    private static final Logger LOG = LoggerFactory.getLogger(MeetjestadClientTest.class);

    public static void main(String[] args) throws IOException {
        MeetjestadClientTest test = new MeetjestadClientTest();
        test.testDownload();
    }

    public void testDownload() throws IOException {
        HostConnectionConfig config = new HostConnectionConfig("https://meetjestad.net", 30);
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        MeetjestadClient client = MeetjestadClient.create(config, mapper);

        Instant from = Instant.now().minusSeconds(900);
        MeetjestadData data = client.download(from);
        List<MeetjestadDataEntry> entries = data.getEntries();
        LOG.info("Got {} total entries", entries.size());

        entries = entries.stream().filter(e -> e.hasLocation() && e.hasPm()).toList();
        LOG.info("Got {} PM entries with location", entries.size());

        // find unique entries
        Map<Integer, MeetjestadDataEntry> map = new HashMap<>();
        for (MeetjestadDataEntry entry : entries) {
            map.put(entry.id, entry);
        }
        LOG.info("Got {} unique entries", map.size());
    }

}
