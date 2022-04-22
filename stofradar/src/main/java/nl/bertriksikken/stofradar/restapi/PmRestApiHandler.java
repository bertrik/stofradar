package nl.bertriksikken.stofradar.restapi;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.stofradar.render.SensorValue;

public final class PmRestApiHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(PmRestApiHandler.class);

    private final Server server;
    
    public PmRestApiHandler(PmRestApiConfig config, Map<String, SensorValue> map) {
        PmRestApi.initialize(config.getMaxDistance(), map);
        this.server = createRestServer(config.getPort(), config.getPath(), PmRestApi.class);
    }
    
    public void start() throws IOException {
        LOG.info("Starting PM REST server");
        try {
            server.start();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    
    public void stop() {
        LOG.info("Stopping PM REST server");
        try {
            server.stop();
        } catch (Exception e) {
            LOG.error("Caught exception during shutdown: {}", e.getMessage());
            LOG.trace("Caught exception during shutdown", e);
        }
    }

    private Server createRestServer(int port, String contextPath, Class<?> clazz) {
        Server server = new Server(port);

        // setup context
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        
        // setup web services container
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, clazz.getCanonicalName());
        context.addServlet(sh, contextPath + "/*");
        server.setHandler(context);
        return server;
    }



}
