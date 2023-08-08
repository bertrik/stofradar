package nl.bertriksikken.stofradar.restapi;

import java.io.IOException;

public final class RunAirRestServer {

    public static void main(String[] args) throws IOException {
        AirRestApiConfig config = new AirRestApiConfig();
        AirRestServer server = new AirRestServer(config);
        server.start();
    }
    
}
