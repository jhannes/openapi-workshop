package com.soprasteria.workshop.openapi.controllers;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class TestDataGenerator {
    public static final Instant START_TIME = OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
            .toInstant();
    private final Random random;
    public TestDataGenerator(long seed) {
        this.random = new Random(seed);
    }

    public TestDataGenerator(Optional<Method> testMethod) {
        this(testMethod.map(m -> m.getName().hashCode()).orElse(100));
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public void nextBytes(byte[] bytes) {
        random.nextBytes(bytes);
    }

    public boolean nextBoolean() {
        return random.nextBoolean();
    }


    public int randomInt(String model, String field) {
        return random.nextInt(1000);
    }

    public boolean randomBoolean(String model, String field) {
        return random.nextBoolean();
    }

    public OffsetDateTime randomDateTime(String model, String field) {
        return randomInstant(model, field).atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    private Instant randomInstant(String model, String field) {
        return START_TIME.plusSeconds(random.nextInt(60*60*24*365*5)).truncatedTo(ChronoUnit.SECONDS);
    }

    public UUID randomUUID(String model, String field) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return UUID.nameUUIDFromBytes(bytes);
    }

    public List<String> randomStringList(String model, String field) {
        List<String> strings = new ArrayList<>();
        int length = random.nextInt(5) + 1;
        for (int i = 0; i < length; i++) {
            strings.add(randomString(model, field));
        }
        return strings;
    }

    public String randomString(String model, String field) {
        return pickOne(RANDOM_WORDS);
    }

    public String randomEmail(String model, String field) {
        return pickOne(List.of("julie", "james", "jamie", "jackie", "jack", "jasmine"))
                + "@" + randomHostname(model, field);
    }

    private String randomHostname(String mode, String field) {
        return pickOne(RANDOM_WORDS) + ".example." + pickOne(List.of("org", "com", "net", "io"));
    }

    public <T> T pickOne(List<T> options) {
        return options.get(random.nextInt(options.size()));
    }

    public String randomPhone(String model, String field) {
        return "5555" + random.nextInt(999) + 1000;
    }

    static final List<String> RANDOM_WORDS = List.of("lovely", "minddesert", "hotfabulous", "badtrick", "carvequickest", "slowshivering", "rulealcoholic", "sulkyagreement", "bashfulfluttering", "untidyflawless", "highfalutin");

}
