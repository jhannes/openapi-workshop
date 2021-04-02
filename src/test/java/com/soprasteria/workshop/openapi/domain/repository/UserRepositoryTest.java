package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest extends AbstractDatabaseTest {
    
    private final UserRepository repository = new UserRepository(context);
    
    @Test
    void shouldRetrieveSavedUsers() {
        User user1 = sampleData.sampleUser();
        User user2 = sampleData.sampleUser();
        repository.save(user1);
        repository.save(user2);
        
        assertThat(repository.query().stream())
                .extracting(User::getId)
                .contains(user1.getId(), user2.getId());
    }
    
    
}
