package com.soprasteria.workshop.openapi.controllers;

import com.soprasteria.workshop.openapi.generated.petstore.CategoryDto;
import com.soprasteria.workshop.openapi.generated.petstore.OrderDto;
import com.soprasteria.workshop.openapi.generated.petstore.PetDto;
import com.soprasteria.workshop.openapi.generated.petstore.UserDto;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class ApiSampleData {
    private final TestDataGenerator generator;

    public ApiSampleData(long seed) {
        this(new TestDataGenerator(seed));
    }

    public ApiSampleData(TestDataGenerator generator) {
        this.generator = generator;
    }

    public ApiSampleData(Optional<Method> testMethod) {
        this(new TestDataGenerator(testMethod));
    }

    public UserDto sampleUserDto() {
        return new UserDto()
                .username(generator.randomString("user", "username"))
                .firstName(generator.randomString("user", "firstName"))
                .lastName(generator.randomString("user", "lastName"))
                .password(generator.randomString("user", "password"))
                .phone(generator.randomPhone("user", "phone"))
                .email(generator.randomEmail("user", "email"));
    }

    public PetDto samplePetDto() {
        return new PetDto()
                .name(generator.randomString("pet", "name"))
                .description(generator.randomString("pet", "description"))
                .category(sampleCategoryDto())
                .tags(generator.randomStringList("pet", "tags"))
                .status(generator.pickOne(List.of(PetDto.StatusEnum.values())));
    }

    public OrderDto sampleOrderDto() {
        return new OrderDto()
                .complete(generator.randomBoolean("order", "complete"))
                .petId(generator.randomUUID("order", "petId"))
                .quantity(generator.randomInt("order", "quantity"))
                .status(generator.pickOne(List.of(OrderDto.StatusEnum.values())))
                .shipDate(generator.randomDateTime("order", "shipDate"));
    }

    private CategoryDto sampleCategoryDto() {
        return new CategoryDto()
                .name(generator.randomString("category", "name"))
                .id(generator.randomUUID("category", "id"));
    }

}
