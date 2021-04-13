package com.soprasteria.workshop.openapi;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

public class DataSourceConfiguration {
    public static DataSource create(Map<String, String> props) {
        if (props.isEmpty()) {
            return testDataSource();
        }
        Properties properties = new Properties();
        props.forEach(properties::put);
        HikariDataSource dataSource = new HikariDataSource(new HikariConfig(properties));
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    private static JdbcDataSource testDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test-database;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

}
