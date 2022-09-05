package com.soprasteria.workshop.openapi;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

public class DataSourceConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfiguration.class);

    public static DataSource create(Map<String, String> props) {
        logger.info("Database configuration {}", props);

        Properties properties = new Properties();
        properties.put("jdbcUrl", "jdbc:h2:mem:test-database;DB_CLOSE_DELAY=-1;MODE=MSSQLServer");
        properties.putAll(props);
        HikariDataSource dataSource = new HikariDataSource(new HikariConfig(properties));
        Flyway.configure().dataSource(dataSource).load().migrate();

        return dataSource;
    }

}
