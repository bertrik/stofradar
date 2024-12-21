package nl.bertriksikken.stofradar.restapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.bertriksikken.stofradar.render.SensorValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@JsonInclude(Include.NON_NULL)
final class AirResult {

    @JsonProperty("pm2.5")
    private BigDecimal pm2_5;

    @JsonProperty("lki")
    private BigDecimal lki;

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
        return String.format(Locale.ROOT, "{pm2.5=%s,lki=%s", pm2_5, lki);
    }

    public void setLki(double lki) {
        this.lki = BigDecimal.valueOf(lki).setScale(2, RoundingMode.HALF_UP);
    }

    private record AirSensor(@JsonProperty("id") String id, @JsonProperty("pm2.5") BigDecimal pm25) {
         AirSensor(String id, double val) {
             this(id, BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP));
        }
    }
}
