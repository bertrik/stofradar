package nl.bertriksikken.stofradar.geolocation.unwiredlabs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record UnwiredGeoLocationRequest(@JsonProperty("token") String token, @JsonProperty("address") int address,
                                        @JsonProperty("wifi") List<Wifi> wifi) {

    public UnwiredGeoLocationRequest(String token, int address) {
        this(token, address, new ArrayList<>());
    }

    public void addWifi(String bssId, int signal) {
        wifi.add(new Wifi(bssId, signal));
    }

    public record Wifi(@JsonProperty("bssid") String bssid, @JsonProperty("signal") int signal) {
        public Wifi {
            bssid = Objects.requireNonNullElse(bssid, "");
        }
    }
}
