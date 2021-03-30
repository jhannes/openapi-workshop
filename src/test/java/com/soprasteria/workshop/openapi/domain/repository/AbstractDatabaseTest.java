package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.SampleData;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;

public class AbstractDatabaseTest {
    protected final SampleData sampleData = new SampleData();
    protected final DbContext context = new DbContext();
    private final DataSource dataSource = TestDataSource.create();
    private DbContextConnection contextConnection;

    @BeforeEach
    public void startContext() {
        contextConnection = context.startConnection(dataSource);
    }

    @AfterEach
    public void endContext() {
        contextConnection.close();
    }
}
