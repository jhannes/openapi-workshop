package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.infrastructure.servlet.PetStoreUser;

public class TestPetStoreUser implements PetStoreUser {
    @Override
    public String getName() {
        return "myName";
    }

    @Override
    public String getFirstName() {
        return null;
    }

    @Override
    public String getLastName() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }
}
