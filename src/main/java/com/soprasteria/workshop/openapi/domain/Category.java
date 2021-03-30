package com.soprasteria.workshop.openapi.domain;

import java.util.UUID;

public class Category {
    private String name;
    private UUID id;

    public void setName(String name) {
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
