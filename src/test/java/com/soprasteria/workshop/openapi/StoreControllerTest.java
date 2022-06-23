package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.controllers.ApiSampleData;
import com.soprasteria.workshop.openapi.controllers.StoreController;
import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.repository.AbstractDatabaseTest;
import com.soprasteria.workshop.openapi.domain.repository.CategoryRepository;
import com.soprasteria.workshop.openapi.domain.repository.PetRepository;
import com.soprasteria.workshop.openapi.generated.petstore.OrderDto;
import com.soprasteria.workshop.infrastructure.repository.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StoreControllerTest extends AbstractDatabaseTest {

    private final CategoryRepository categoryRepository = new CategoryRepository(dbContext);
    private final PetRepository petRepository = new PetRepository(dbContext);
    private final StoreController controller = new StoreController(dbContext);
    private ApiSampleData apiSampleData;

    @BeforeEach
    public void sampleData(TestInfo testInfo) {
        apiSampleData = new ApiSampleData(testInfo.getTestMethod());
    }

    @Test
    void shouldPlaceOrder() {
        Category category = new Category("fish");
        categoryRepository.save(category);
        Pet pet = sampleData.samplePet(category);
        petRepository.save(pet);

        OrderDto orderDto = apiSampleData.sampleOrderDto().petId(pet.getId());
        UUID orderId = controller.placeOrder(orderDto);
        assertThat(controller.getOrderById(orderId))
                .isEqualToIgnoringGivenFields(orderDto, "id");
    }

    @Test
    void shouldDeleteOrder() {
        Category category = new Category("fish");
        categoryRepository.save(category);
        Pet pet = sampleData.samplePet(category);
        petRepository.save(pet);

        OrderDto orderDto = apiSampleData.sampleOrderDto().petId(pet.getId());
        UUID id = controller.placeOrder(orderDto);
        controller.deleteOrder(id);
        assertThatThrownBy(() -> controller.getOrderById(id)).isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> controller.deleteOrder(id)).isInstanceOf(EntityNotFoundException.class);
    }

}
