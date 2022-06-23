package com.soprasteria.workshop.openapi.controllers;

import com.soprasteria.workshop.openapi.TestPetStoreUser;
import com.soprasteria.workshop.openapi.domain.repository.AbstractDatabaseTest;
import com.soprasteria.workshop.openapi.generated.petstore.UserDto;
import com.soprasteria.workshop.infrastructure.servlet.PetStoreUser;
import org.actioncontroller.exceptions.HttpUnauthorizedException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserControllerTest extends AbstractDatabaseTest {

    private final UserController controller = new UserController(dbContext);
    private ApiSampleData apiSampleData = new ApiSampleData(200);

    @Test
    void shouldSaveUser() {
        UserDto userDto = apiSampleData.sampleUserDto().password(null);
        String userName = controller.createUser(userDto);
        assertThat(controller.getUserByName(userName))
                .usingRecursiveComparison()
                .isEqualTo(userDto);
    }

    @Test
    void shouldUpdateUser() {
        UserDto originalUser = apiSampleData.sampleUserDto();
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
        PetStoreUser petStoreUser = new TestPetStoreUser();
        assertThat(controller.getCurrentUser(petStoreUser).getUsername())
                .isEqualTo("myName");
    }

    @Test
    void shouldLogRequireCorrectPassword() {
        UserDto user = apiSampleData.sampleUserDto()
                .password("somepass");
        controller.createUser(user);
        AtomicReference<String> sessionUser = new AtomicReference<>();
        assertThatThrownBy(() -> controller.loginUser(user.getUsername(), "wrongpass", sessionUser::set))
                .isInstanceOf(HttpUnauthorizedException.class);
    }


}
