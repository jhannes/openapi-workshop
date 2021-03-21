package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.infrastructure.ContentServlet;
import com.soprasteria.workshop.openapi.infrastructure.WebJarServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class PetStoreApplication implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            setupContext(sce.getServletContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupContext(ServletContext context) throws IOException {
        context.addServlet("hello", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.getWriter().write("Hello world");
            }
        }).addMapping("/hello");
        context.addServlet("content", new WebJarServlet("swagger-ui")).addMapping("/swagger-ui/*");
        context.addServlet("swagger-ui", new ContentServlet("/webapp/")).addMapping("/*");
    }
}
