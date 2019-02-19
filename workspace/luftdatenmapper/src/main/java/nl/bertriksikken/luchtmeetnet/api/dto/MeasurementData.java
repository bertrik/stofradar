package nl.bertriksikken.luchtmeetnet.api.dto;

import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class MeasurementData {

    @JsonProperty("value")
    private double value;

    @JsonProperty("formula")
    private String formula;

    @JsonProperty("timestamp_measured")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private Date timeStamp;

    private MeasurementData() {
        // jackson constructor
    }

    public MeasurementData(double value, String formula, Date timeStamp) {
        this();
        this.value = value;
        this.formula = formula;
        this.timeStamp = new Date(timeStamp.getTime());
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{value=%s,formula=%s,timestamp=%s}", value, formula, timeStamp);
    }

}
