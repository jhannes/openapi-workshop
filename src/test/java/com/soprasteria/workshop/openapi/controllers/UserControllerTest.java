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
    
    @Test
    void shouldUpdateUser() {
        UserDto userDto = new UserDto()
                .username("user-two").email("test2@example.com")
                .firstName("First").lastName("Last")
                .phone("5551234");
        controller.createUser(userDto);
        controller.updateUser(userDto.getUsername(), new UserDto()
                .username("ignored-username-change")
                .firstName("Newfirst").lastName("Newlast")
                .phone("5559876")
        );
        
        assertThat(controller.getUserByName("user-two"))
                .usingRecursiveComparison()
                .isEqualTo(new UserDto()
                        .username("user-two")
                        .email("test2@example.com")
                        .firstName("Newfirst").lastName("Newlast")
                        .phone("5559876"));
    }
    
}