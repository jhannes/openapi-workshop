package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.controllers.ApiSampleData;
import com.soprasteria.workshop.openapi.controllers.PetController;
import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.openapi.domain.repository.AbstractDatabaseTest;
import com.soprasteria.workshop.openapi.domain.repository.CategoryRepository;
import com.soprasteria.workshop.openapi.generated.petstore.CategoryDto;
import com.soprasteria.workshop.openapi.generated.petstore.PetDto;
import com.soprasteria.workshop.openapi.infrastructure.repository.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private final PetController controller = new PetController(dbContext);
    public UUID sampleCategoriId;
    public ApiSampleData apiSampleData;
    private static final String servletUrl = "https://petstore.example.com/petstore/api";

    @BeforeEach
    public void insertCategories(TestInfo testInfo) {
        CategoryRepository repository = new CategoryRepository(dbContext);
        for (String categoryName : CATEGORIES) {
            repository.save(new Category(categoryName));
        }
        sampleCategoriId = sampleData.pickOneFromList(controller.listCategories()).getId();
        apiSampleData = new ApiSampleData(testInfo.getTestMethod());
    }
    
    @Test
    void shouldRetrieveSavedPet() {
        CategoryDto category = sampleData.pickOneFromList(controller.listCategories());
        PetDto petDto = apiSampleData.samplePetDto().category(category);
        UUID petId = controller.addPet(petDto);
        petDto.setId(petId);
        petDto.setCategory(category);

        assertThat(controller.getPetById(petId, servletUrl)).usingRecursiveComparison().isEqualTo(petDto);
    }
    
    @Test
    void throwsOnNotFound() {
        assertThatThrownBy(() -> controller.getPetById(UUID.randomUUID(), servletUrl)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldListPetsByStatus() {
        controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("Available").status(AVAILABLE));
        controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("Pending").status(PENDING));
        controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("Sold").status(SOLD));
        
        assertThat(controller.findPetsByStatus(Optional.of(List.of(AVAILABLE, PENDING)), servletUrl))
                .extracting(PetDto::getName)
                .contains("Available", "Pending")
                .doesNotContain("Sold");
    }
    
    @Test
    void shouldDeletePet() {
        UUID petId = controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("To be deleted"));
        controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("To be retained"));
        
        controller.deletePet(petId);
        assertThat(controller.findPetsByStatus(Optional.empty(), servletUrl))
                .extracting(PetDto::getName)
                .contains("To be retained")
                .doesNotContain("To be deleted");
    }

    @Test
    void shouldUpdatePet() {
        List<CategoryDto> categories = controller.listCategories().collect(Collectors.toList());
        UUID categoryId = categories.get(0).getId();
        UUID petId = controller.addPet(apiSampleData.samplePetDto().category(new CategoryDto().id(categoryId)));

        PetDto updatedPet = apiSampleData.samplePetDto().category(categories.get(1));
        controller.updatePet(petId, updatedPet);
        updatedPet.setCategory(categories.get(1));
        updatedPet.setId(petId);
        assertThat(controller.getPetById(petId, servletUrl))
                .usingRecursiveComparison()
                .isEqualTo(updatedPet);
    }
    
    @Test
    void shouldUpdatePetWithForm() {
        UUID petId = controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("To be updated"));
        
        controller.updatePetWithForm(petId, Optional.of("New Name"), Optional.empty());
        assertThat(controller.getPetById(petId, servletUrl).getName()).isEqualTo("New Name");

        controller.updatePetWithForm(petId, Optional.empty(), Optional.of(PENDING.getValue()));
        assertThat(controller.getPetById(petId, servletUrl).getStatus()).isEqualTo(PENDING);
    }
    
    @Test
    void uploadImage() throws IOException {
        UUID petId = controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("To be updated"));
        byte[] redDot = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==");
        UUID fileId = controller.uploadFile(petId, new ByteArrayInputStream(redDot), "reddot.png");
        assertThat(controller.getPetById(petId, servletUrl).getPhotoUrls())
                .contains(servletUrl + "/pet/images/" + fileId);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        controller.getImage(fileId).transferTo(buffer);
        assertThat(buffer.toByteArray()).isEqualTo(redDot);
    }
    
    @Test
    void shouldFindPetsByTags() {
        UUID pet1Id = controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("A").tags(List.of("tag1", "tag2")));
        UUID pet2Id = controller.addPet(new PetDto().category(new CategoryDto().id(sampleCategoriId)).name("B").tags(List.of("tag2", "tag3")));
        
        assertThat(controller.findPetsByTags(List.of("tag1"), servletUrl)).extracting(PetDto::getId)
                .contains(pet1Id).doesNotContain(pet2Id);
        assertThat(controller.findPetsByTags(List.of("tag2"), servletUrl)).extracting(PetDto::getId)
                .contains(pet1Id, pet2Id);
        assertThat(controller.findPetsByTags(List.of("tag1", "tag3"), servletUrl)).extracting(PetDto::getId)
                .contains(pet1Id, pet2Id);
    }
}