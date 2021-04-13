package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.controllers.PetController;
import com.soprasteria.workshop.openapi.controllers.StoreController;
import com.soprasteria.workshop.openapi.controllers.UserController;
import com.soprasteria.workshop.openapi.infrastructure.servlet.ContentServlet;
import com.soprasteria.workshop.openapi.infrastructure.servlet.ContentSource;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration;
import org.actioncontroller.jakarta.ApiJakartaServlet;
import org.fluentjdbc.DbContext;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

public class PetStoreApplication implements ServletContextListener {
    
    private final PetController petController;
    private final StoreController storeController;
    private final UserController userController;
    public PetApiFilter filter;

    public PetStoreApplication(DbContext dbContext) {
        petController = new PetController(dbContext);
        filter = new PetApiFilter(dbContext);
        storeController = new StoreController(dbContext);
        userController = new UserController(dbContext);
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
        ServletRegistration.Dynamic apiRegistration = context.addServlet("api", new ApiJakartaServlet(List.of(petController, storeController, userController)));
        apiRegistration.setMultipartConfig(new MultipartConfigElement("./tmp"));
        apiRegistration.addMapping("/api/*");
        context.addServlet("content", new ContentServlet(ContentSource.fromWebJar("swagger-ui")))
                .addMapping("/swagger-ui/*");
        context.addServlet("swagger-ui", new ContentServlet("/webapp/")).addMapping("/*");
        
        context.addFilter("apiFilter", filter)
                .addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST), false, "api");
    }

    public void setDataSource(DataSource dataSource) {
        filter.setDataSource(dataSource);
    }
}
