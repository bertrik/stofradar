package nl.bertriksikken.stofradar.samenmeten.csv;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;

/**
 * Representation of one line of
 * https://samenmeten.rivm.nl/dataportaal/php/getData-fromfile.php?compartiment=lucht
 */
@JsonPropertyOrder({ "timestamp", "locationName", "locationCode", "project", "latitude", "longitude", "pm10", "pm2_5",
        "temperature", "humidity", "pressure" })
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class SamenmetenCsvLuchtEntry {

    private static final Logger LOG = LoggerFactory.getLogger(SamenmetenCsvLuchtEntry.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("loc_name")
    private String locationName;
    @JsonProperty("loc_code")
    private String locationCode;
    @JsonProperty("project")
    private String project;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("pm10")
    private Double pm10;
    @JsonProperty("pm2_5")
    private Double pm2_5;
    @JsonProperty("temperature")
    private Double temperature;
    @JsonProperty("humidity")
    private Double humidity;
    @JsonProperty("pressure")
    private Double pressure;

    SamenmetenCsvLuchtEntry(String timestamp, String locationName, String locationCode, String project, Double latitude,
            Double longitude, Double pm10, Double pm2_5, Double temperature, Double humidity, Double pressure) {
        this.timestamp = timestamp;
        this.locationName = locationName;
        this.locationCode = locationCode;
        this.project = project;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pm10 = pm10;
        this.pm2_5 = pm2_5;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
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
            Double longitude = parseDouble(node.at("/coordinates/0").asText());
            Double latitude = parseDouble(node.at("/coordinates/1").asText());
            Double pm10 = parseDouble(items.get(5));
            Double pm2_5 = parseDouble(items.get(6));
            Double temperature = parseDouble(items.get(9));
            Double humidity = parseDouble(items.get(10));
            Double pressure = parseDouble(items.get(11));
            return new SamenmetenCsvLuchtEntry(timestamp, locName, locCode, project, latitude, longitude, pm10, pm2_5,
                    temperature, humidity, pressure);
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to parse CSV line '{}'", line);
            return null;
        }
    }

    // returns value parsed as double, or null if not possible
    private static Double parseDouble(String s) {
        if (s != null) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return null;
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
        return getDouble(latitude);
    }

    public double getLongitude() {
        return getDouble(longitude);
    }

    public double getPm10() {
        return getDouble(pm10);
    }

    public double getPm2_5() {
        return getDouble(pm2_5);
    }

    // returns value as double, null is converted to Double.NaN
    private static double getDouble(Double value) {
        return value != null ? value : Double.NaN;
    }
    
}
