package nl.bertriksikken.stofradar.meetjestad;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.stofradar.render.SensorValue;

/**
 * Data as downloaded from meetjestad.
 */
public final class MeetjestadData {

    private final List<MeetjestadDataEntry> entries;

    MeetjestadData(List<MeetjestadDataEntry> entries) {
        this.entries = List.copyOf(entries);
    }

    public List<MeetjestadDataEntry> getEntries() {
        return List.copyOf(entries);
    }

    /**
     * @return the data as a list of SensorValue objects
     */
    public List<SensorValue> toSensorValues() {
        List<SensorValue> values = new ArrayList<>();
        for (MeetjestadDataEntry entry : entries) {
            if (entry.hasLocation() && entry.hasPm()) {
                SensorValue value = new SensorValue("mjs_" + entry.id, entry.longitude, entry.latitude, entry.pm2_5,
                        entry.getTimestamp());
                values.add(value);
            }
        }
        return values;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class MeetjestadDataEntry {

        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC);

        @JsonProperty("id")
        public final int id;

        @JsonProperty("timestamp")
        private final String timestamp;

        @JsonProperty("latitude")
        private final double latitude;

        @JsonProperty("longitude")
        private final double longitude;

        @JsonProperty("pm2.5")
        private final double pm2_5;

        @JsonProperty("pm10")
        private final double pm10;

        // no-arg jackson constructor
        private MeetjestadDataEntry() {
            id = 0;
            timestamp = "";
            latitude = Double.NaN;
            longitude = Double.NaN;
            pm2_5 = Double.NaN;
            pm10 = Double.NaN;
        }

        Instant getTimestamp() {
            return ZonedDateTime.parse(timestamp, DATE_FORMATTER).toInstant();
        }

        boolean hasLocation() {
            return Double.isFinite(latitude) && Double.isFinite(longitude);
        }

        boolean hasPm() {
            return Double.isFinite(pm2_5) && Double.isFinite(pm10);
        }
    }
}
