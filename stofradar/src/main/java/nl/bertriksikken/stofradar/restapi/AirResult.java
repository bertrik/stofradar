package nl.bertriksikken.stofradar.restapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.bertriksikken.stofradar.render.SensorValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(Include.NON_NULL)
final class AirResult {

    @JsonProperty("pm2.5")
    private BigDecimal pm2_5;

    @JsonProperty("sensors")
    private final List<AirSensor> sensors = new ArrayList<>();

    private AirResult() {
        // jackson constructor
    }

    AirResult(double pm2_5) {
        this();
        if (Double.isFinite(pm2_5)) {
            this.pm2_5 = BigDecimal.valueOf(pm2_5).setScale(2, RoundingMode.HALF_UP);
        }
    }

    void addSensor(SensorValue value) {
        sensors.add(new AirSensor(value.id, value.value, value.y, value.x));
    }

    @Override
    public String toString() {
        return String.valueOf(pm2_5);
    }

    private record AirSensor(@JsonProperty("id") String id, @JsonProperty("pm2.5") BigDecimal pm25,
                             @JsonProperty("lat") BigDecimal lat, @JsonProperty("lon") BigDecimal lon) {
        AirSensor(String id, double val, double lat, double lon) {
            this(id, BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(lat).setScale(5, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(lon).setScale(5, RoundingMode.HALF_UP));
        }
    }
}
