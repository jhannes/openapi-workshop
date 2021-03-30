package com.soprasteria.workshop.openapi.domain.repository;

import org.fluentjdbc.DatabaseSaveResult;

import java.util.UUID;

public interface Repository<T> {
    DatabaseSaveResult.SaveStatus save(T o);

    Query<T> query();

    T retrieve(UUID id);
}
