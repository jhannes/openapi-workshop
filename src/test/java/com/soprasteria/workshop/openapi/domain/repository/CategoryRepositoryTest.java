package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Category;
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

class CategoryRepositoryTest {
    
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
    
    private CategoryRepository repository = new CategoryRepository(context);
    
    @Test
    void shouldListSavedEntites() {
        Category category1 = sampleCategory();
        Category category2 = sampleCategory();
        
        repository.save(category1);
        repository.save(category2);
        
        assertThat(repository.query().list())
                .extracting(Category::getName)
                .contains(category1.getName(), category2.getName());
    }
    
    @Test
    void shouldRetrievePetProperties() {
        Category category = sampleCategory();
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
    

    private Category sampleCategory() {
        Category category = new Category();
        category.setName(randomCategoryName());
        return category;
    }

    private String randomCategoryName() {
        return pickOne("Cat", "Dog", "Hamster", "Parrot", "Goldfish", "Guinea Pig");
    }

    private static Random random = new Random();

    @SafeVarargs
    private static <T> T pickOne(T... alternatives) {
        return alternatives[random.nextInt(alternatives.length)];
    }

}