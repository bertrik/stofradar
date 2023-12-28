package nl.bertriksikken.stofradar.render;

import java.util.stream.Stream;

public enum EDataSource {
    SENSOR_COMMUNITY("sensor.community"), MEETJESTAD("meetjestad"), SAMENMETEN("samenmeten");

    private final String name;

    EDataSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EDataSource fromName(String name) {
        return Stream.of(EDataSource.values()).filter(v -> v.name.equals(name)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return name;
    }
}
