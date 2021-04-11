package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.openapi.domain.repository.CategoryRepository;
import com.soprasteria.workshop.openapi.infrastructure.Slf4jRequestLog;
import jakarta.servlet.ServletContextListener;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
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
import org.fluentjdbc.DbContextConnection;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;

import java.util.Optional;

public class PetStoreServer {
    
    private final Server server = new Server();
    private final DbContext context = new DbContext();
    private final PetStoreApplication petStoreApplication = new PetStoreApplication(context);

    public static void main(String[] args) throws Exception {
        new PetStoreServer().start();
    }

    private void start() throws Exception {
        JdbcDataSource dataSource = testDataSource();
        petStoreApplication.setDataSource(dataSource);
        
        HandlerList handlers = new HandlerList();
        handlers.addHandler(createContext("/petstore", petStoreApplication));
        handlers.addHandler(new MovedContextHandler(null, "/", "/petstore"));
        server.setHandler(handlers);
        server.setRequestLog(new Slf4jRequestLog());
        int port = Optional.ofNullable(System.getenv("HTTP_PLATFORM_PORT")).map(Integer::parseInt)
                .orElse(8080);
        server.addConnector(createConnector(server, port));
        server.start();
    }

    private Connector createConnector(Server server, int port) {
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        connector.addConnectionFactory(createConnectionFactory());
        return connector;
    }

    private ConnectionFactory createConnectionFactory() {
        HttpConfiguration config = new HttpConfiguration();
        config.addCustomizer(new ForwardedRequestCustomizer());
        return new HttpConnectionFactory(config);
    }


    private JdbcDataSource testDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test-database;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        try (DbContextConnection ignored = context.startConnection(dataSource)) {
            CategoryRepository repository = new CategoryRepository(context);
            repository.save(new Category("cat"));
            repository.save(new Category("dog"));
        }
        return dataSource;
    }

    private Handler createContext(String contextPath, ServletContextListener contextListener) {
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath(contextPath);
        servletContextHandler.addEventListener(contextListener);
        return servletContextHandler;
    }

}
