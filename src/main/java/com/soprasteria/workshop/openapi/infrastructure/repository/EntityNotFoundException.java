package com.soprasteria.workshop.openapi.infrastructure.repository;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> entityClass, UUID id) {
        this(entityClass.getSimpleName(), id);
    }

    public EntityNotFoundException(String entityType, UUID id) {
        this(entityType, id.toString());
    }

    public EntityNotFoundException(String entityType, String id) {
        super("Not found " + entityType + " with id " + id);
    }
}
