package nl.bertriksikken.stofradar.restapi;

import nl.bertriksikken.stofradar.gelocation.GeoLocationResource;

public final class RunAirRestServer {

    public static void main(String[] args) {
        AirRestApiConfig config = new AirRestApiConfig();

        AirResource airResource = new AirResource(config);
        airResource.start();

        AirRestServer server = new AirRestServer(config);
        server.registerResource(airResource);
        server.registerResource(new GeoLocationResource());
        server.start();
    }
    
}
