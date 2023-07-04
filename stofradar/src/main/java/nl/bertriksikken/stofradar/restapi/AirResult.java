package nl.bertriksikken.stofradar.restapi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.stofradar.render.SensorValue;

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
        sensors.add(new AirSensor(value.id, value.value));
    }

    @Override
    public String toString() {
        return String.valueOf(pm2_5);
    }

    private static final class AirSensor {
        @JsonProperty("id")
        private final String id;

        @JsonProperty("pm2.5")
        private final BigDecimal val;

        private AirSensor(String id, double val) {
            this.id = id;
            this.val = BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
        }
    }
}
