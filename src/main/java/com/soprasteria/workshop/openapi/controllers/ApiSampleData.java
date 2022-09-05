package com.soprasteria.workshop.openapi.controllers;

import com.soprasteria.workshop.openapi.generated.petstore.PetDto;
import com.soprasteria.workshop.openapi.generated.petstore.SampleModelData;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class ApiSampleData extends SampleModelData {

    public ApiSampleData(Optional<Method> testMethod) {
        super(testMethod.map(m -> m.getName().hashCode()).orElse(-1));
        propertyNameFactories.put("firstName", this::randomGivenName);
        propertyNameFactories.put("lastName", this::randomFamilyName);
    }

    @Override
    public PetDto samplePetDto() {
        return super.samplePetDto().category(null).photoUrls(List.of());
    }
}
