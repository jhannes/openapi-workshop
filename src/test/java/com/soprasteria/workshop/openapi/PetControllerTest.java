package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.openapi.domain.repository.AbstractDatabaseTest;
import com.soprasteria.workshop.openapi.domain.repository.CategoryRepository;
import com.soprasteria.workshop.openapi.generated.petstore.CategoryDto;
import com.soprasteria.workshop.openapi.generated.petstore.PetDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.soprasteria.workshop.openapi.domain.SampleData.CATEGORIES;
import static com.soprasteria.workshop.openapi.generated.petstore.PetDto.StatusEnum.AVAILABLE;
import static com.soprasteria.workshop.openapi.generated.petstore.PetDto.StatusEnum.PENDING;
import static com.soprasteria.workshop.openapi.generated.petstore.PetDto.StatusEnum.SOLD;
import static org.assertj.core.api.Assertions.assertThat;

class PetControllerTest extends AbstractDatabaseTest {
    private static final Random random = new Random();

    private final PetController controller = new PetController(context);

    @BeforeEach
    public void insertCategories() {
        CategoryRepository repository = new CategoryRepository(context);
        for (String categoryName : CATEGORIES) {
            repository.save(new Category(categoryName));
        }
    }
    
    @Test
    void shouldRetrieveSavedPet() {
        CategoryDto category = pickOneFromList(controller.listCategories());
        PetDto petDto = new PetDto()
                .category(new CategoryDto().id(category.getId()))
                .name(randomName())
                .status(pickOne(PetDto.StatusEnum.values()))
                .tags(List.of("tag1", "tag2"))
                .addPhotoUrlsItem("http://example.com/photo.jpg");
        UUID petId = controller.addPet(petDto);
        petDto.setId(petId);
        petDto.setCategory(category);

        assertThat(controller.getPetById(petId)).usingRecursiveComparison().isEqualTo(petDto);
    }
    
    @Test
    void shouldListPetsByStatus() {
        CategoryDto category = pickOneFromList(controller.listCategories());
        controller.addPet(new PetDto().category(new CategoryDto().id(category.getId())).name("Available").status(AVAILABLE));
        controller.addPet(new PetDto().category(new CategoryDto().id(category.getId())).name("Pending").status(PENDING));
        controller.addPet(new PetDto().category(new CategoryDto().id(category.getId())).name("Sold").status(SOLD));
        
        assertThat(controller.findPetsByStatus(Optional.of(List.of(AVAILABLE.getValue(), PENDING.getValue()))))
                .extracting(PetDto::getName)
                .contains("Available", "Pending")
                .doesNotContain("Sold");
    }
    

    private <T> T pickOneFromList(Stream<T> alternatives) {
        return pickOneFromList(alternatives.collect(Collectors.toList()));
    }

    private <T> T pickOneFromList(List<T> alternatives) {
        return alternatives.get(random.nextInt(alternatives.size()));
    }

    private String randomName() {
        return pickOne("Bella", "Luna", "Charlie", "Lucy", "Cooper", "Max", "Bailey", "Daisy") + " " + random.nextInt(100);
    }

    @SafeVarargs
    private static <T> T pickOne(T... alternatives) {
        return alternatives[random.nextInt(alternatives.length)];
    }

}