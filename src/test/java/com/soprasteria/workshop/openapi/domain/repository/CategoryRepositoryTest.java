package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.infrastructure.repository.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryRepositoryTest extends AbstractDatabaseTest {

    private final CategoryRepository repository = new CategoryRepository(dbContext);

    @Test
    void shouldListSavedEntities() {
        Category category1 = sampleData.sampleCategory();
        Category category2 = sampleData.sampleCategory();

        repository.save(category1);
        repository.save(category2);

        assertThat(repository.query().stream())
                .extracting(Category::getName)
                .contains(category1.getName(), category2.getName());
    }

    @Test
    void shouldRetrievePetProperties() {
        Category category = sampleData.sampleCategory();
        repository.save(category);
        assertThat(repository.retrieve(category.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(category);
    }

    @Test
    void shouldThrowOnUnknownId() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> repository.retrieve(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Not found Category with id " + id);
    }

}
