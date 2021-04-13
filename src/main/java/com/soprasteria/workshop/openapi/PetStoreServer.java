package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.infrastructure.Slf4jRequestLog;
import jakarta.servlet.ServletContextListener;
import org.actioncontroller.config.ConfigObserver;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.MovedContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.fluentjdbc.DbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Optional;

public class PetStoreServer {
    
    private static final Logger logger = LoggerFactory.getLogger(PetStoreServer.class);
    
    private final Server server = new Server();
    private final ServerConnector connector = new ServerConnector(server);
    private final DbContext context = new DbContext();
    private final PetStoreApplication petStoreApplication = new PetStoreApplication(context);

    public static void main(String[] args) throws Exception {
        new PetStoreServer().start();
    }

    private void start() throws Exception {
        connector.addConnectionFactory(createConnectionFactory());

        server.setHandler(createHandlers());
        server.setRequestLog(new Slf4jRequestLog());
        server.start();

        setupConfiguration();
    }

    private void setupConfiguration() {
        int port = Optional.ofNullable(System.getenv("HTTP_PLATFORM_PORT")).map(Integer::parseInt)
                .orElse(8080);

        new ConfigObserver("petstore")
                .onInetSocketAddress("http.port", port, this::setHttpPort)
                .onPrefixedValue("dataSource", DataSourceConfiguration::create, petStoreApplication::setDataSource);
    }

    private HandlerList createHandlers() {
        HandlerList handlers = new HandlerList();
        handlers.addHandler(createContext("/petstore", petStoreApplication));
        handlers.addHandler(new MovedContextHandler(null, "/", "/petstore"));
        return handlers;
    }

    private Handler createContext(String contextPath, ServletContextListener contextListener) {
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath(contextPath);
        servletContextHandler.addEventListener(contextListener);
        MimeTypes mimeTypes = servletContextHandler.getMimeTypes();
        mimeTypes.addMimeMapping(".js.map", "application/octet-stream");
        mimeTypes.addMimeMapping(".yaml", "text/yaml");
        mimeTypes.addMimeMapping(".yml", "text/yaml");
        return servletContextHandler;
    }

    private void setHttpPort(InetSocketAddress address) throws Exception {
        if (connector.getPort() == address.getPort()) {
            return;
        }
        connector.setPort(address.getPort());
        if (server.isStarted()) {
            connector.stop();
            connector.start();
            logger.warn("Started on {}", getURI());
        }
    }

    public URI getURI() {
        return server.getURI();
    }


    private ConnectionFactory createConnectionFactory() {
        HttpConfiguration config = new HttpConfiguration();
        config.addCustomizer(new ForwardedRequestCustomizer());
        return new HttpConnectionFactory(config);
    }

}
