package com.soprasteria.workshop.openapi.domain;

import java.util.Random;

public class SampleData {

    public static final String[] CATEGORIES = {"Cat", "Dog", "Hamster", "Parrot", "Goldfish", "Guinea Pig"};

    public Pet samplePet(Category category) {
        Pet pet = new Pet();
        pet.setName(randomName());
        pet.setCategoryId(category.getId());
        pet.setStatus(pickOne(PetStatus.values()));
        return pet;
    }

    public Category sampleCategory() {
        return new Category(randomCategoryName());
    }

    public String randomCategoryName() {
        return pickOne(CATEGORIES);
    }

    public String randomName() {
        return pickOne("Bella", "Luna", "Charlie", "Lucy", "Cooper", "Max", "Bailey", "Daisy") + " " + random.nextInt(100);
    }

    private static Random random = new Random();

    @SafeVarargs
    public static <T> T pickOne(T... alternatives) {
        return alternatives[random.nextInt(alternatives.length)];
    }
}
