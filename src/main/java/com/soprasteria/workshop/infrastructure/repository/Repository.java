package com.soprasteria.workshop.infrastructure.repository;

import org.fluentjdbc.DatabaseSaveResult;

import java.sql.SQLException;
import java.util.UUID;

public interface Repository<T> {
    DatabaseSaveResult.SaveStatus save(T o) throws SQLException;

    T retrieve(UUID id);

    Query<T> query();

    void delete(T o);
}
