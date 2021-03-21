package com.soprasteria.workshop.openapi;

import jakarta.servlet.ServletContextListener;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class PetStoreServer {
    
    private final Server server = new Server(8080);

    public static void main(String[] args) throws Exception {
        new PetStoreServer().start();
    }

    private void start() throws Exception {
        HandlerList handlers = new HandlerList();
        handlers.addHandler(createContext("/petstore", new PetStoreApplication()));
        server.setHandler(handlers);
        server.start();
    }

    private Handler createContext(String contextPath, ServletContextListener contextListener) {
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath(contextPath);
        servletContextHandler.addEventListener(contextListener);
        return servletContextHandler;
    }

}
