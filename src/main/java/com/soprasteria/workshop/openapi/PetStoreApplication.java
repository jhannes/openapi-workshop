package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.infrastructure.ContentServlet;
import com.soprasteria.workshop.openapi.infrastructure.WebJarServlet;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.actioncontroller.jakarta.ApiJakartaServlet;
import org.fluentjdbc.DbContext;
import org.h2.jdbcx.JdbcDataSource;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

public class PetStoreApplication implements ServletContextListener {
    
    private final PetController petController;
    private final StoreController storeController = new StoreController();
    private final UserController userController = new UserController();
    public PetApiFilter filter;

    public PetStoreApplication(DbContext dbContext) {
        petController = new PetController(dbContext);
        filter = new PetApiFilter(dbContext);
    }

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
        
        context.addFilter("apiFilter", filter)
                .addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST), false, "api");
    }

    public void setDataSource(JdbcDataSource dataSource) {
        filter.setDataSource(dataSource);
    }
}
