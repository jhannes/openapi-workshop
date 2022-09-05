package com.soprasteria.workshop.openapi.controllers;

import com.soprasteria.workshop.openapi.TestPetStoreUser;
import com.soprasteria.workshop.openapi.domain.repository.AbstractDatabaseTest;
import com.soprasteria.workshop.openapi.generated.petstore.UserDto;
import com.soprasteria.workshop.infrastructure.servlet.PetStoreUser;
import org.actioncontroller.exceptions.HttpUnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UserControllerTest extends AbstractDatabaseTest {

    private final UserController controller = new UserController(dbContext);
    private final ApiSampleData apiSampleData;

    public UserControllerTest(TestInfo testInfo) {
        this.apiSampleData = new ApiSampleData(testInfo.getTestMethod());
    }

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
        UserDto updatedUser = apiSampleData.sampleUserDto().username("ignored-username-change").email(null).password(null);
        controller.updateUser(username, updatedUser);

        assertThat(controller.getUserByName(username))
                .usingRecursiveComparison()
                .ignoringFields("username", "email")
                .isEqualTo(updatedUser);
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
