package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.infrastructure.repository.EntityNotFoundException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Optional;

public class PetApiFilter implements Filter {
    private final DbContext dbContext;
    private Optional<DataSource> dataSource = Optional.empty();

    public PetApiFilter(DbContext dbContext) {
        this.dbContext = dbContext;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try (DbContextConnection ignored = dbContext.startConnection(getDataSource())) {
            chain.doFilter(request, response);
        } catch (EntityNotFoundException e) {
            ((HttpServletResponse)response).sendError(404, e.getMessage());
        }
    }

    private DataSource getDataSource() {
        return dataSource.orElseThrow(() -> new IllegalStateException("database not setup"));
    }

    public void setDataSource(JdbcDataSource dataSource) {
        this.dataSource = Optional.of(dataSource);
    }
}
