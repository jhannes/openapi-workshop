package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.PetEntity;
import com.soprasteria.workshop.infrastructure.repository.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PetRepositoryTest extends AbstractDatabaseTest {

    private final PetRepository repository = new PetRepository(dbContext);
    public Category category;

    @BeforeEach
    public void saveCategory() {
        this.category = sampleData.sampleCategory();
        new CategoryRepository(dbContext).save(category);
    }

    @Test
    void shouldListSavedPets() {
        Pet pet1 = sampleData.samplePet(category);
        Pet pet2 = sampleData.samplePet(category);
        repository.save(pet1);
        repository.save(pet2);

        assertThat(repository.query().stream())
                .extracting(Pet::getName)
                .contains(pet1.getName(), pet2.getName());
    }

    @Test
    void shouldRetrievePetProperties() {
        Pet pet = sampleData.samplePet(category);
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

    @Test
    void shouldRetrievePetEntity() {
        Pet pet = sampleData.samplePet(category);
        repository.save(pet);
        repository.saveTags(pet, List.of("tag1", "tag2"));
        byte[] redDot = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==");
        UUID imageId = repository.saveImage(pet, "reddot.png", new ByteArrayInputStream(redDot));

        PetEntity entity = repository.retrieveEntity(pet.getId());
        assertThat(entity.getPet()).usingRecursiveComparison().isEqualTo(pet);
        assertThat(entity.getCategory()).usingRecursiveComparison().isEqualTo(category);
        assertThat(entity.getTags()).containsExactly("tag1", "tag2");
        assertThat(entity.getImages()).containsExactly(imageId);
    }

}
