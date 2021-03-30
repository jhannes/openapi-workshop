package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.PetStatus;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PetRepositoryTest {
    
    private DbContext context = new DbContext();
    private DataSource dataSource = TestDataSource.create();
    private DbContextConnection contextConnection;

    @BeforeEach
    public void startContext() {
        contextConnection = context.startConnection(dataSource);
    }
    
    @AfterEach
    public void endContext() {
        contextConnection.close();
    }
    
    private PetRepository repository = new PetRepository(context);
    
    @Test
    void shouldListSavedPets() {
        Pet pet1 = samplePet();
        Pet pet2 = samplePet();
        repository.save(pet1);
        repository.save(pet2);
        
        assertThat(repository.query().list())
                .extracting(Pet::getName)
                .contains(pet1.getName(), pet2.getName());
    }
    
    @Test
    void shouldRetrievePetProperties() {
        Pet pet = samplePet();
        repository.save(pet);
        assertThat(repository.retrieve(pet.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(pet);
    }
    
    @Test
    void shouldThrowOnUnknownId() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> repository.retrieve(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Not found Pet with id " + id);
    }
    

    private Pet samplePet() {
        Pet pet = new Pet();
        pet.setName(pickOne("Bella", "Luna", "Charlie", "Lucy", "Cooper", "Max", "Bailey", "Daisy") + " " + random.nextInt(100));
        pet.setCategoryId(UUID.randomUUID());
        pet.setStatus(pickOne(PetStatus.values()));
        return pet;
    }
    
    private static Random random = new Random();

    @SafeVarargs
    private static <T> T pickOne(T... alternatives) {
        return alternatives[random.nextInt(alternatives.length)];
    }

}