package nl.bertriksikken.stofradar.samenmeten.csv;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;

/**
 * Representation of one line of
 * https://samenmeten.rivm.nl/dataportaal/php/getData-fromfile.php?compartiment=lucht
 */
public final class SamenmetenCsvLuchtEntry {

    private static final Logger LOG = LoggerFactory.getLogger(SamenmetenCsvLuchtEntry.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String timestamp;
    private String locationName;
    private String locationCode;
    private String project;
    private double latitude;
    private double longitude;
    private double pm10;
    private double pm2_5;

    SamenmetenCsvLuchtEntry(String timestamp, String locationName, String locationCode, String project, double latitude,
            double longitude, double pm10, double pm2_5) {
        this.timestamp = timestamp;
        this.locationName = locationName;
        this.locationCode = locationCode;
        this.project = project;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pm10 = pm10;
        this.pm2_5 = pm2_5;
    }

    public boolean hasValidLocation() {
        return Double.isFinite(latitude) && Double.isFinite(longitude);
    }

    /**
     * Parses one line of the Samenmeten CSV-like structure.
     * 
     * @param line the line, excluding the line delimiter ";"
     * @return a parsed SamenmetenCsvLuchtEntry, or null if it could not be parsed
     */
    public static SamenmetenCsvLuchtEntry parse(String line) {
        try {
            List<String> items = Splitter.on(", ").trimResults().splitToList(line);
            if (items.size() < 15) {
                return null;
            }
            String timestamp = items.get(0);
            String locName = items.get(1);
            String locCode = items.get(2);
            String project = items.get(3);
            JsonNode node = MAPPER.readTree(items.get(4));
            double longitude = parseDouble(node.at("/coordinates/0").asText());
            double latitude = parseDouble(node.at("/coordinates/1").asText());
            double pm10 = parseDouble(items.get(5));
            double pm2_5 = parseDouble(items.get(6));
            return new SamenmetenCsvLuchtEntry(timestamp, locName, locCode, project, latitude, longitude, pm10, pm2_5);
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to parse CSV line '{}'", line);
            return null;
        }
    }

    private static double parseDouble(String s) {
        if (s != null) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return Double.NaN;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public String getProject() {
        return project;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getPm10() {
        return pm10;
    }

    public double getPm2_5() {
        return pm2_5;
    }

}
