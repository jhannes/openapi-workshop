package com.soprasteria.workshop.openapi.domain.repository;

import org.fluentjdbc.DatabaseSaveResult;

public interface Repository<T> {
    DatabaseSaveResult.SaveStatus save(T o);

    Query<T> query();
}
