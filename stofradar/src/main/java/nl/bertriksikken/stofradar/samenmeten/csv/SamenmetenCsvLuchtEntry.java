package nl.bertriksikken.stofradar.samenmeten.csv;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Representation of one line of
 * https://samenmeten.rivm.nl/dataportaal/php/getData-fromfile.php?compartiment=lucht
 */
@JsonPropertyOrder({ "time", "kit_id", "label", "project", "geom_lat", "geom_lon", "pm10", "pm25", "no2", "no2_palmes",
        "temp", "rh", "pres", "nh3_palmes", "pm10_kwal", "pm25_kwal" })
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class SamenmetenCsvLuchtEntry {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @JsonProperty("time")
    private final String timestamp;
    @JsonProperty("kit_id")
    private final String kitId;
    @JsonProperty("label")
    private final String label;
    @JsonProperty("project")
    private final String project;
    @JsonProperty("geom_lat")
    private final Double latitude;
    @JsonProperty("geom_lon")
    private final Double longitude;
    @JsonProperty("pm10")
    private final Double pm10;
    @JsonProperty("pm25")
    private final Double pm25;
    @JsonProperty("no2")
    private final Double no2;
    @JsonProperty("no2_palmes")
    private final Double no2Palmes;
    @JsonProperty("temp")
    private final Double temperature;
    @JsonProperty("rh")
    private final Double humidity;
    @JsonProperty("pres")
    private final Double pressure;
    @JsonProperty("nh3_palmes")
    private Object nh3Palmes;
    @JsonProperty("pm10_kwal")
    private int pm10Kwal;
    @JsonProperty("pm25_kwal")
    private int pm25Kwal;

    SamenmetenCsvLuchtEntry(String timestamp, String kitId, String label, String project, Double latitude,
            Double longitude, Double pm10, Double pm25, Double no2, Double no2Palmes, Double temperature,
            Double humidity, Double pressure, Double nh3Palmes, int pm10_kwal, int pm25_kwal) {
        this.timestamp = timestamp;
        this.kitId = kitId;
        this.label = label;
        this.project = project;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.no2 = no2;
        this.no2Palmes = no2Palmes;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.nh3Palmes = nh3Palmes;
        this.pm10Kwal = pm10_kwal;
        this.pm25Kwal = pm25_kwal;
    }

    public static SamenmetenCsvLuchtEntry from(List<String> fields)
            throws JsonMappingException, JsonProcessingException {
        if (fields.size() < 16) {
            return null;
        }
        String timestamp = fields.get(0);
        String kitId = fields.get(1);
        String label = fields.get(2);
        String project = fields.get(3);
        JsonNode geom = MAPPER.readTree(fields.get(4));
        Double longitude = getDouble(geom.at("/coordinates/0").asText());
        Double latitude = getDouble(geom.at("/coordinates/1").asText());
        Double pm10 = getDouble(fields.get(5));
        Double pm25 = getDouble(fields.get(6));
        Double no2 = getDouble(fields.get(7));
        Double no2Palmes = getDouble(fields.get(8));
        Double temp = getDouble(fields.get(9));
        Double rh = getDouble(fields.get(10));
        Double pres = getDouble(fields.get(11));
        Double nh3Palmes = getDouble(fields.get(12));
        int pm10Kwal = getInteger(fields.get(13), -1);
        int pm25Kwal = getInteger(fields.get(14), -1);
        return new SamenmetenCsvLuchtEntry(timestamp, kitId, label, project, latitude, longitude, pm10, pm25, no2,
                no2Palmes, temp, rh, pres, nh3Palmes, pm10Kwal, pm25Kwal);
    }

    private static Double getDouble(String s) {
        return s.isEmpty() ? Double.NaN : Double.valueOf(s);
    }

    private static Integer getInteger(String s, int defaultValue) {
        return s.isEmpty() ? defaultValue : Integer.valueOf(s);
    }

    public boolean hasValidLocation() {
        return Double.isFinite(latitude) && Double.isFinite(longitude);
    }

    public Instant getTimestamp() {
        return ZonedDateTime.parse(timestamp, DATE_FORMATTER).toInstant();
    }

    public String getLocationName() {
        return kitId;
    }

    public String getLocationCode() {
        return label;
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
        return pm25;
    }

}
