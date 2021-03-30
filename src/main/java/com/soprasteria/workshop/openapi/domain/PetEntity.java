package com.soprasteria.workshop.openapi.domain;

import java.util.List;

public class PetEntity {
    private final Pet pet;
    private final Category category;
    private final List<String> tags;
    private final List<String> urls;

    public PetEntity(Pet pet, Category category, List<String> tags, List<String> urls) {
        this.pet = pet;
        this.category = category;
        this.tags = tags;
        this.urls = urls;
    }

    public Pet getPet() {
        return pet;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getUrls() {
        return urls;
    }

    public Category getCategory() {
        return category;
    }
}
