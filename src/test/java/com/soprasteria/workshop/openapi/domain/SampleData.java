package com.soprasteria.workshop.openapi.domain;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SampleData {

    public static final String[] CATEGORIES = {"Cat", "Dog", "Hamster", "Parrot", "Goldfish", "Guinea Pig"};

    public Pet samplePet(Category category) {
        Pet pet = new Pet();
        pet.setName(randomName());
        pet.setDescription(randomName() + " test");
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

    private static final Random random = new Random();

    public <T> T pickOneFromList(Stream<T> alternatives) {
        return pickOneFromList(alternatives.collect(Collectors.toList()));
    }

    public <T> T pickOneFromList(List<T> alternatives) {
        return alternatives.get(random.nextInt(alternatives.size()));
    }

    @SafeVarargs
    public static <T> T pickOne(T... alternatives) {
        return alternatives[random.nextInt(alternatives.length)];
    }

    private static int index = 1;

    public User sampleUser() {
        User user = new User();
        user.setUsername(randomUsername());
        user.setFirstName(randomFirstName());
        user.setLastName(randomLastName());
        user.setEmail(randomEmail());
        user.setPhone(randomPhoneNumber());
        return user;
    }

    public String randomPhoneNumber() {
        return "5555" + random.nextInt(999) + 1000;
    }

    public String randomEmail() {
        return randomFirstName() + (index++) + "@" + randomLastName() + ".example.com";
    }

    public String randomUsername() {
        return "user" + index++;
    }

    private String randomLastName() {
        return pickOne("Juleson", "Anderson", "Jameson");
    }

    public String randomFirstName() {
        return pickOne("Julie", "Anders", "Johannes");
    }
}
