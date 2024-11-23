package nl.bertriksikken.stofradar.restapi;

public final class RunAirRestServer {

    public static void main(String[] args) {
        AirRestApiConfig config = new AirRestApiConfig();
        AirRestServer server = new AirRestServer(config);
        AirResource resource = new AirResource(config);
        resource.start();
        server.registerResource(resource);
        server.start();
    }
    
}
