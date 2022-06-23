package com.soprasteria.workshop.openapi.infrastructure.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;
import org.actioncontroller.ApiControllerContext;
import org.actioncontroller.ApiHttpExchange;
import org.actioncontroller.jakarta.JakartaServletHttpExchange;
import org.actioncontroller.meta.HttpParameterMapper;
import org.actioncontroller.meta.HttpParameterMapperFactory;
import org.actioncontroller.meta.HttpParameterMapping;
import org.actioncontroller.util.ExceptionUtil;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Parameter;

@Retention(RetentionPolicy.RUNTIME)
@HttpParameterMapping(Multipart.MappingFactory.class)
public @interface Multipart {
    String value();

    @Retention(RetentionPolicy.RUNTIME)
    @HttpParameterMapping(Multipart.FilenameMappingFactory.class)
    @interface Filename {
        String value();
    }

    class MappingFactory implements HttpParameterMapperFactory<Multipart> {
        @Override
        public HttpParameterMapper create(Multipart annotation, Parameter parameter, ApiControllerContext context) {
            return exchange -> getPart(exchange, annotation.value()).getInputStream();
        }

        static Part getPart(ApiHttpExchange exchange, String name) throws IOException {
            try {
                return ((JakartaServletHttpExchange) exchange).getRequest().getPart(name);
            } catch (ServletException e) {
                throw ExceptionUtil.softenException(e);
            }
        }
    }

    class FilenameMappingFactory implements HttpParameterMapperFactory<Filename> {
        @Override
        public HttpParameterMapper create(Filename annotation, Parameter parameter, ApiControllerContext context) {
            return exchange -> MappingFactory.getPart(exchange, annotation.value()).getSubmittedFileName();
        }
    }
}
