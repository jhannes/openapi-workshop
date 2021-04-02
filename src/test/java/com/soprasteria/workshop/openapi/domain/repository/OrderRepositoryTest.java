package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.openapi.domain.Order;
import com.soprasteria.workshop.openapi.domain.OrderStatus;
import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.SampleData;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryTest extends AbstractDatabaseTest {

    private final OrderRepository repository = new OrderRepository(context);
    private final PetRepository petRepository = new PetRepository(context);
    private final CategoryRepository categoryRepository = new CategoryRepository(context);

    @Test
    void shouldRetrievePetProperties() {
        Order order = sampleOrder();
        repository.save(order);
        assertThat(repository.retrieve(order.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(order);
    }

    private Order sampleOrder() {
        Category category = sampleData.sampleCategory();
        categoryRepository.save(category);
        Pet pet = sampleData.samplePet(category);
        petRepository.save(pet);
        Order order = new Order();
        order.setPetId(pet.getId());
        order.setShipDate(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        order.setQuantity(100);
        order.setOrderStatus(SampleData.pickOne(OrderStatus.values()));
        order.setComplete(false);
        return order;
    }

}