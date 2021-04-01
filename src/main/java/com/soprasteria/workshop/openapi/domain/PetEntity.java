package com.soprasteria.workshop.openapi.domain;

import java.util.List;
import java.util.UUID;

public class PetEntity {
    private final Pet pet;
    private final Category category;
    private final List<String> tags;
    private final List<UUID> images;

    public PetEntity(Pet pet, Category category, List<String> tags, List<UUID> images) {
        this.pet = pet;
        this.category = category;
        this.tags = tags;
        this.images = images;
    }

    public Pet getPet() {
        return pet;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<UUID> getImages() {
        return images;
    }

    public Category getCategory() {
        return category;
    }
}
