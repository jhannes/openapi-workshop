package com.soprasteria.workshop.openapi.controllers;

import com.soprasteria.workshop.openapi.domain.repository.AbstractDatabaseTest;
import com.soprasteria.workshop.openapi.generated.petstore.UserDto;
import org.actioncontroller.HttpUnauthorizedException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserControllerTest extends AbstractDatabaseTest {

    private final UserController controller = new UserController(dbContext);
    
    @Test
    void shouldSaveUser() {
        UserDto userDto = sampleUserDto();
        String userName = controller.createUser(userDto);
        assertThat(controller.getUserByName(userName))
                .usingRecursiveComparison()
                .isEqualTo(userDto);
    }

    @Test
    void shouldUpdateUser() {
        UserDto originalUser = sampleUserDto();
        controller.createUser(originalUser);
        String username = originalUser.getUsername();
        controller.updateUser(username, new UserDto()
                .username("ignored-username-change")
                .firstName("Newfirst").lastName("Newlast")
                .phone("5559876")
        );
        
        assertThat(controller.getUserByName(username))
                .usingRecursiveComparison()
                .isEqualTo(new UserDto()
                        .username(username)
                        .email(originalUser.getEmail())
                        .firstName("Newfirst").lastName("Newlast")
                        .phone("5559876"));
    }
    
    @Test
    void shouldLogUserIn() {
        UserDto user = sampleUserDto();
        controller.createUser(user);
        AtomicReference<String> sessionUser = new AtomicReference<>();
        controller.loginUser(user.getUsername(), "randompass", sessionUser::set);
        assertThat(controller.getCurrentUser(sessionUser.get()))
                .usingRecursiveComparison()
                .isEqualTo(user);
        controller.logoutUser(sessionUser::set);
        assertThatThrownBy(() -> controller.getCurrentUser(sessionUser.get()))
                .isInstanceOf(HttpUnauthorizedException.class);
    }

    private UserDto sampleUserDto() {
        return new UserDto()
                .username(sampleData.randomUsername())
                .email(sampleData.randomEmail())
                .firstName(sampleData.randomFirstName())
                .lastName(sampleData.randomFirstName())
                .phone(sampleData.randomPhoneNumber());
    }


}