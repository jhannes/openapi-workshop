package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.repository.AbstractDatabaseTest;
import com.soprasteria.workshop.openapi.domain.repository.CategoryRepository;
import com.soprasteria.workshop.openapi.domain.repository.PetRepository;
import com.soprasteria.workshop.openapi.generated.petstore.OrderDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StoreControllerTest extends AbstractDatabaseTest {

    private final CategoryRepository categoryRepository = new CategoryRepository(context);
    private final PetRepository petRepository = new PetRepository(context);
    private final StoreController controller = new StoreController(context);
    
    @Test
    void shouldPlaceOrder() {
        Category category = new Category("fish");
        categoryRepository.save(category);
        Pet pet = new Pet();
        pet.setName("Goldfish");
        pet.setCategoryId(category.getId());
        petRepository.save(pet);

        OrderDto orderDto = new OrderDto()
                .complete(false)
                .petId(pet.getId())
                .quantity(2)
                .status(OrderDto.StatusEnum.APPROVED)
                .shipDate(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        UUID orderId = controller.placeOrder(orderDto);
        assertThat(controller.getOrderById(orderId))
                .isEqualToIgnoringGivenFields(orderDto, "id");
    }
    
}