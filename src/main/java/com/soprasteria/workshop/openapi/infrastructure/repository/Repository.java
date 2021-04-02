package com.soprasteria.workshop.openapi.infrastructure.repository;

import org.fluentjdbc.DatabaseSaveResult;

import java.util.UUID;

public interface Repository<T> {
    DatabaseSaveResult.SaveStatus save(T o);

    T retrieve(UUID id);

    Query<T> query();
    
    void delete(T o);
}
