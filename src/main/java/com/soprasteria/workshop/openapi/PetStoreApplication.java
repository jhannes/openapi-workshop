package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.infrastructure.ContentServlet;
import com.soprasteria.workshop.openapi.infrastructure.WebJarServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.actioncontroller.jakarta.ApiJakartaServlet;

import java.io.IOException;
import java.util.List;

public class PetStoreApplication implements ServletContextListener {
    
    private final PetController petController = new PetController();
    private final StoreController storeController = new StoreController();
    private final UserController userController = new UserController();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            setupContext(sce.getServletContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupContext(ServletContext context) throws IOException {
        context.addServlet("api", new ApiJakartaServlet(List.of(petController, storeController, userController))).addMapping("/api/*");
        context.addServlet("content", new WebJarServlet("swagger-ui")).addMapping("/swagger-ui/*");
        context.addServlet("swagger-ui", new ContentServlet("/webapp/")).addMapping("/*");
    }
}
