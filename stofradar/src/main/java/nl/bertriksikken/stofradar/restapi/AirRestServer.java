package nl.bertriksikken.stofradar.restapi;

import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

public final class AirRestServer {

    private static final Logger LOG = LoggerFactory.getLogger(AirRestServer.class);

    private final Server server;
    private final AirResource resource;

    public AirRestServer(AirRestApiConfig config) {
        this.resource = new AirResource(config);

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(resource);
        resourceConfig.property(ServerProperties.WADL_FEATURE_DISABLE, true);
        URI uri = UriBuilder.fromUri("http://localhost").port(config.getPort()).build();
        this.server = JettyHttpContainerFactory.createServer(uri, resourceConfig);
    }

    public void start() throws IOException {
        LOG.info("Starting Air REST server");
        try {
            resource.start();
            server.start();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void stop() {
        LOG.info("Stopping Air REST server");
        try {
            server.stop();
            resource.stop();
        } catch (Exception e) {
            LOG.error("Caught exception during shutdown: {}", e.getMessage());
            LOG.trace("Caught exception during shutdown", e);
        }
    }

}
