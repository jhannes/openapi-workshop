package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.infrastructure.Slf4jRequestLog;
import com.soprasteria.workshop.infrastructure.repository.EntityNotFoundException;
import com.soprasteria.workshop.infrastructure.servlet.OpenIdConnectAuthentication;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.slf4j.MDC;

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
        Request req = (Request) request;
        Response resp = (Response) response;
        MDC.clear();
        MDC.put("request", Slf4jRequestLog.getRequest(req));
        MDC.put("remoteAddress", req.getRemoteAddr());
        resp.setHeader("Access-Control-Allow-Origin", "*");

        if (req.getMethod().equals("OPTIONS")) {
            resp.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            resp.setStatus(204);
            return;
        }

        req.setAuthentication(new OpenIdConnectAuthentication());

        try (DbContextConnection ignored = dbContext.startConnection(getDataSource())) {
            chain.doFilter(request, response);
        } catch (EntityNotFoundException e) {
            ((HttpServletResponse)response).sendError(404, e.getMessage());
        }
    }

    private DataSource getDataSource() {
        return dataSource.orElseThrow(() -> new IllegalStateException("database not setup"));
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = Optional.of(dataSource);
    }
}
