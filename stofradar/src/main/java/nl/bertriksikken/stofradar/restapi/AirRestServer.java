package nl.bertriksikken.stofradar.restapi;

import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Objects;

public final class AirRestServer {

    private static final Logger LOG = LoggerFactory.getLogger(AirRestServer.class);

    private final AirRestApiConfig config;
    private final ResourceConfig resourceConfig = new ResourceConfig();
    private Server server;

    public AirRestServer(AirRestApiConfig config) {
        this.config = Objects.requireNonNull(config);
        resourceConfig.property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }

    public void registerResource(Object resource) {
        resourceConfig.register(resource);
    }

    public void start() {
        LOG.info("Starting Air REST server");
        URI uri = UriBuilder.fromUri("http://localhost").port(config.getPort()).build();
        server = JettyHttpContainerFactory.createServer(uri, resourceConfig);
    }

    public void stop() throws Exception {
        LOG.info("Stopping Air REST server");
        server.stop();
    }

}
