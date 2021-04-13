package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.SampleData;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;

public class AbstractDatabaseTest {
    protected final SampleData sampleData = new SampleData();
    protected final DbContext dbContext = new DbContext();
    private final DataSource dataSource = createDataSource();
    private DbContextConnection contextConnection;

    private static DataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MSSQLServer");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    @BeforeEach
    public void startContext() {
        contextConnection = dbContext.startConnection(dataSource);
    }

    @AfterEach
    public void endContext() {
        contextConnection.close();
    }
}
