package com.soprasteria.workshop.openapi.controllers;

import com.soprasteria.workshop.openapi.domain.repository.AbstractDatabaseTest;
import com.soprasteria.workshop.openapi.generated.petstore.UserDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class UserControllerTest extends AbstractDatabaseTest {

    private final UserController controller = new UserController(dbContext);
    
    @Test
    void shouldSaveUser() {
        UserDto userDto = new UserDto()
                .username("user-one").email("test@example.com")
                .firstName("First").lastName("Last")
                .phone("5551234");
        String userName = controller.createUser(userDto);
        assertThat(controller.getUserByName(userName))
                .usingRecursiveComparison()
                .isEqualTo(userDto);
    }
    
}