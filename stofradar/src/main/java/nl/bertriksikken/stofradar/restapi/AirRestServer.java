package nl.bertriksikken.stofradar.restapi;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import es.moki.ratelimitj.inmemory.request.InMemorySlidingWindowRequestRateLimiter;
import jakarta.ws.rs.core.UriBuilder;

public final class AirRestServer {

    private static final Logger LOG = LoggerFactory.getLogger(AirRestServer.class);

    private final Server server;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AirRestServer(AirRestApiConfig config) {
        this.server = createRestServer(config.getPort());

        RequestLimitRule rule = RequestLimitRule.of(Duration.ofSeconds(60), 10).withPrecision(Duration.ofSeconds(3));
        RequestRateLimiter rateLimiter = new InMemorySlidingWindowRequestRateLimiter(Collections.singleton(rule));
        AirRestApi.initialize(executorService, config.getMaxDistance(), rateLimiter);
    }

    public void start() throws IOException {
        LOG.info("Starting Air REST server");
        try {
            server.start();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void stop() {
        LOG.info("Stopping Air REST server");
        try {
            server.stop();
            executorService.shutdownNow();
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error("Caught exception during shutdown: {}", e.getMessage());
            LOG.trace("Caught exception during shutdown", e);
        }
    }

    private Server createRestServer(int port) {
        LOG.info("Setting up Air REST service on {}", port);

        URI uri = UriBuilder.fromUri("http://localhost").port(port).build();
        ResourceConfig config = new ResourceConfig(AirRestApi.class);
        config.property(ServerProperties.WADL_FEATURE_DISABLE, true);
        return JettyHttpContainerFactory.createServer(uri, config);
    }

}
