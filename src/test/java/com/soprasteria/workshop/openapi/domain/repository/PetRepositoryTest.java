package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.PetEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PetRepositoryTest extends AbstractDatabaseTest {

    private final PetRepository repository = new PetRepository(context);
    public Category category;

    @BeforeEach
    public void saveCategory() {
        this.category = sampleData.sampleCategory();
        new CategoryRepository(context).save(category);
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
        repository.saveUrls(pet, List.of("http://example.com/test.jpg", "data:image/png;base64,iVBORw0KGgoAA"));
        
        PetEntity entity = repository.retrieveEntity(pet.getId());
        assertThat(entity.getPet()).usingRecursiveComparison().isEqualTo(pet);
        assertThat(entity.getCategory()).usingRecursiveComparison().isEqualTo(category);
        assertThat(entity.getTags()).containsExactly("tag1", "tag2");
        assertThat(entity.getUrls()).containsExactly("http://example.com/test.jpg", "data:image/png;base64,iVBORw0KGgoAA");
    }

}