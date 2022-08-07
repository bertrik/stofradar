package nl.bertriksikken.stofradar.restapi;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import es.moki.ratelimitj.inmemory.request.InMemorySlidingWindowRequestRateLimiter;
import nl.bertriksikken.stofradar.render.SensorValue;

public final class AirRestServer {

    private static final Logger LOG = LoggerFactory.getLogger(AirRestServer.class);

    private final Server server;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AirRestServer(AirRestApiConfig config, Map<String, SensorValue> map) {
        this.server = createRestServer(config.getPort(), config.getPath(), AirRestApi.class);

        RequestLimitRule rule = RequestLimitRule.of(Duration.ofSeconds(30), 1).withPrecision(Duration.ofSeconds(3));
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

    private Server createRestServer(int port, String contextPath, Class<?> clazz) {
        LOG.info("Setting up Air REST service on {}", port);
        Server server = new Server(port);

        // setup context
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

        // suppress sending the exact jetty version
        for (Connector connector : server.getConnectors()) {
            for (org.eclipse.jetty.server.ConnectionFactory connectionFactory : connector.getConnectionFactories()) {
                if (connectionFactory instanceof HttpConnectionFactory) {
                    HttpConnectionFactory httpConnectionFactory = (HttpConnectionFactory) connectionFactory;
                    httpConnectionFactory.getHttpConfiguration().setSendServerVersion(false);
                }
            }
        }

        // setup web services container
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, clazz.getCanonicalName());
        context.addServlet(sh, contextPath + "/*");
        server.setHandler(context);
        return server;
    }

}
