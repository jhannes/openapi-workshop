package com.soprasteria.workshop.openapi.domain;

import java.util.UUID;

public class Category {
    private final String name;
    private UUID id;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
