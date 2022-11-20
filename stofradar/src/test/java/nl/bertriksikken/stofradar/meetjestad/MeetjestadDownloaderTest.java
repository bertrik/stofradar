package nl.bertriksikken.stofradar.meetjestad;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.stofradar.meetjestad.MeetjestadData.MeetjestadDataEntry;

/**
 * Downloads past 15 minutes of meetjestad data.
 */
public final class MeetjestadDownloaderTest {

    private static final Logger LOG = LoggerFactory.getLogger(MeetjestadDownloaderTest.class);

    public static void main(String args[]) throws IOException {
        MeetjestadDownloaderTest test = new MeetjestadDownloaderTest();
        test.testDownload();
    }

    public void testDownload() throws IOException {
        MeetjestadConfig config = new MeetjestadConfig();
        MeetjestadDownloader downloader = MeetjestadDownloader.create(config);

        Instant from = Instant.now().minusSeconds(900);
        MeetjestadData data = downloader.download(from);
        List<MeetjestadDataEntry> entries = data.getEntries();
        LOG.info("Got {} total entries", entries.size());

        entries = entries.stream().filter(e -> e.hasLocation() && e.hasPm()).collect(Collectors.toList());
        LOG.info("Got {} PM entries with location", entries.size());

        // find unique entries
        Map<Integer, MeetjestadDataEntry> map = new HashMap<>();
        for (MeetjestadDataEntry entry : entries) {
            map.put(entry.id, entry);
        }
        LOG.info("Got {} unique entries", map.size());
    }

}
