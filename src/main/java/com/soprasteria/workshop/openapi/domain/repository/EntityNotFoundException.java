package com.soprasteria.workshop.openapi.domain.repository;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> entityClass, UUID id) {
        super("Not found " + entityClass.getSimpleName() + " with id " + id);
    }
}
