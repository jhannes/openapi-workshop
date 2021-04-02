package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.controllers.PetController;
import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.openapi.domain.SampleData;
import com.soprasteria.workshop.openapi.domain.repository.AbstractDatabaseTest;
import com.soprasteria.workshop.openapi.domain.repository.CategoryRepository;
import com.soprasteria.workshop.openapi.infrastructure.repository.EntityNotFoundException;
import com.soprasteria.workshop.openapi.generated.petstore.CategoryDto;
import com.soprasteria.workshop.openapi.generated.petstore.PetDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.soprasteria.workshop.openapi.domain.SampleData.CATEGORIES;
import static com.soprasteria.workshop.openapi.generated.petstore.PetDto.StatusEnum.AVAILABLE;
import static com.soprasteria.workshop.openapi.generated.petstore.PetDto.StatusEnum.PENDING;
import static com.soprasteria.workshop.openapi.generated.petstore.PetDto.StatusEnum.SOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PetControllerTest extends AbstractDatabaseTest {

    private final PetController controller = new PetController(context);
    public UUID sampleCategoriId;

    @BeforeEach
    public void insertCategories() {
        CategoryRepository repository = new CategoryRepository(context);
        for (String categoryName : CATEGORIES) {
            repository.save(new Category(categoryName));
        }
        sampleCategoriId = sampleData.pickOneFromList(controller.listCategories()).getId();
    }
    
    @Test
    void shouldRetrieveSavedPet() {
        CategoryDto category = sampleData.pickOneFromList(controller.listCategories());
        PetDto petDto = new PetDto()
                .category(new CategoryDto().id(category.getId()))
                .name(sampleData.randomName())
                .status(SampleData.pickOne(PetDto.StatusEnum.values()))
                .tags(List.of("tag1", "tag2"));
        UUID petId = controller.addPet(petDto);
        petDto.setId(petId);
        petDto.setCategory(category);

        assertThat(controller.getPetById(petId)).usingRecursiveComparison().isEqualTo(petDto);
    }
    
    @Test
    void throwsOnNotFound() {
        assertThatThrownBy(() -> controller.getPetById(UUID.randomUUID())).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldListPetsByStatus() {
        controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("Available").status(AVAILABLE));
        controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("Pending").status(PENDING));
        controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("Sold").status(SOLD));
        
        assertThat(controller.findPetsByStatus(Optional.of(List.of(AVAILABLE.getValue(), PENDING.getValue()))))
                .extracting(PetDto::getName)
                .contains("Available", "Pending")
                .doesNotContain("Sold");
    }
    
    @Test
    void shouldDeletePet() {
        UUID petId = controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("To be deleted"));
        controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("To be retained"));
        
        controller.deletePet(petId);
        assertThat(controller.findPetsByStatus(Optional.empty()))
                .extracting(PetDto::getName)
                .contains("To be retained")
                .doesNotContain("To be deleted");
    }

    @Test
    void shouldUpdatePet() {
        List<CategoryDto> categories = controller.listCategories().collect(Collectors.toList());
        UUID categoryId = categories.get(0).getId();
        UUID petId = controller.addPet(new PetDto()
                .category(new CategoryDto().id(categoryId))
                .name("To be updated")
                .tags(List.of("tag1", "tag2"))
                .status(AVAILABLE)
        );

        PetDto updatedPet = new PetDto()
                .id(petId)
                .category(new CategoryDto().id(categories.get(1).getId()))
                .name("Updated name")
                .tags(List.of("tag3", "tag4"))
                .status(SOLD);
        controller.updatePet(petId, updatedPet);
        updatedPet.setCategory(categories.get(1));
        assertThat(controller.getPetById(petId))
                .usingRecursiveComparison()
                .isEqualTo(updatedPet);
    }
    
    @Test
    void shouldUpdatePetWithForm() {
        UUID petId = controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("To be updated"));
        
        controller.updatePetWithForm(petId, Optional.of("New Name"), Optional.empty());
        assertThat(controller.getPetById(petId).getName()).isEqualTo("New Name");

        controller.updatePetWithForm(petId, Optional.empty(), Optional.of(PENDING.getValue()));
        assertThat(controller.getPetById(petId).getStatus()).isEqualTo(PENDING);
    }
    
    @Test
    void uploadImage() throws IOException {
        UUID petId = controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("To be updated"));
        byte[] redDot = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==");
        UUID fileId = controller.uploadFile(petId, new ByteArrayInputStream(redDot), "reddot.png");
        assertThat(controller.getPetById(petId).getPhotoUrls())
                .contains("/pet/images/" + fileId);
        assertThat(controller.getImage(fileId)).isEqualTo(redDot);
    }
    
    @Test
    void shouldFindPetsByTags() {
        UUID pet1Id = controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("A").tags(List.of("tag1", "tag2")));
        UUID pet2Id = controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("B").tags(List.of("tag2", "tag3")));
        
        assertThat(controller.findPetsByTags(List.of("tag1"))).extracting(PetDto::getId)
                .contains(pet1Id).doesNotContain(pet2Id);
        assertThat(controller.findPetsByTags(List.of("tag2"))).extracting(PetDto::getId)
                .contains(pet1Id, pet2Id);
        assertThat(controller.findPetsByTags(List.of("tag1", "tag3"))).extracting(PetDto::getId)
                .contains(pet1Id, pet2Id);
    }
}