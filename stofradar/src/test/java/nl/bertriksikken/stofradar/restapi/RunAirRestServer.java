package nl.bertriksikken.stofradar.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.stofradar.config.HostConnectionConfig;
import nl.bertriksikken.stofradar.geolocation.GeoLocationResource;
import nl.bertriksikken.stofradar.geolocation.IGeoLocator;
import nl.bertriksikken.stofradar.geolocation.beacondb.BeacondbClient;

public final class RunAirRestServer {

    public static void main(String[] args) {
        AirRestApiConfig config = new AirRestApiConfig();

        AirResource airResource = new AirResource(config);
        airResource.start();

        AirRestServer server = new AirRestServer(config);
        server.registerResource(airResource);

        IGeoLocator geoLocator = BeacondbClient.create(new HostConnectionConfig("https://api.beacondb.net", 30),
                new ObjectMapper(), "github.com/bertrik/stofradar");
        server.registerResource(new GeoLocationResource(geoLocator));
        server.start();
    }

}
