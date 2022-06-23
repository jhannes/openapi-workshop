package com.soprasteria.workshop.openapi.controllers;

import com.soprasteria.workshop.openapi.domain.User;
import com.soprasteria.workshop.openapi.domain.repository.UserRepository;
import com.soprasteria.workshop.openapi.generated.petstore.UserDto;
import com.soprasteria.workshop.openapi.infrastructure.repository.EntityNotFoundException;
import com.soprasteria.workshop.openapi.infrastructure.servlet.PetStoreUser;
import org.actioncontroller.actions.DELETE;
import org.actioncontroller.actions.GET;
import org.actioncontroller.actions.POST;
import org.actioncontroller.actions.PUT;
import org.actioncontroller.exceptions.HttpUnauthorizedException;
import org.actioncontroller.values.ContentLocationHeader;
import org.actioncontroller.values.PathParam;
import org.actioncontroller.values.RequestParam;
import org.actioncontroller.values.SessionParameter;
import org.actioncontroller.values.UserPrincipal;
import org.actioncontroller.values.json.JsonBody;
import org.fluentjdbc.DbContext;

import java.util.List;
import java.util.function.Consumer;

public class UserController {

    private final UserRepository repository;

    public UserController(DbContext dbContext) {
        repository = new UserRepository(dbContext);
    }

    /**
     * Create user
     * This can only be done by the logged in user.
     *
     * @param userDto Created user object (optional)
     */
    @POST("/user")
    @ContentLocationHeader("/user/{username}")
    public String createUser(@JsonBody UserDto userDto) {
        User o = new User();
        o.setUsername(userDto.getUsername());
        o.setEmail(userDto.getEmail());
        o.setFirstName(userDto.getFirstName());
        o.setLastName(userDto.getLastName());
        o.setPhone(userDto.getPhone());
        o.setPassword(userDto.getPassword());
        repository.save(o);
        return o.getUsername();
    }

    /**
     * Creates list of users with given input array
     *
     * @param userDto List of user object (optional
     */
    @POST("/user/createWithList")
    public void createUsersWithListInput(@JsonBody List<UserDto> userDto) {

    }

    /**
     * Delete user
     * This can only be done by the logged in user.
     *
     * @param username The name that needs to be deleted (required)
     */
    @DELETE("/user/{username}")
    public void deleteUser(@PathParam("username") String username) {

    }

    /**
     * Get user by user name
     *
     * @param username The name that needs to be fetched. Use user1 for testing.  (required)
     * @return UserDto
     */
    @GET("/user/{username}")
    @JsonBody
    public UserDto getUserByName(
            @PathParam("username") String username
    ) {
        return repository.query().username(username).single()
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User", username));
    }

    /**
     * Get user by user name
     */
    @GET("/user/current")
    @JsonBody
    public UserDto getCurrentUser(@UserPrincipal PetStoreUser principal) {
        return new UserDto()
                .username(principal.getName())
                .firstName(principal.getFirstName())
                .lastName(principal.getLastName())
                .email(principal.getEmail());
    }

    private UserDto toDto(User user) {
        return new UserDto()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone());
    }

    /**
     * Logs user into the system
     *
     * @param username The user name for login (optional)
     * @param password The password for login in clear text (optional)
     */
    @GET("/user/login")
    @JsonBody
    public void loginUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @SessionParameter(value = "username", invalidate = true) Consumer<String> setUserSession
    ) {
        User user = repository.query().username(username).single()
                .orElseThrow(() -> new EntityNotFoundException("User", username));
        if (!user.isCorrectPassword(password)) {
            throw new HttpUnauthorizedException();
        }
        setUserSession.accept(username);
    }

    /**
     * Logs out current logged in user session
     */
    @GET("/user/logout")
    public void logoutUser(
            @SessionParameter(value = "username", invalidate = true) Consumer<String> setUserSession
    ) {
        setUserSession.accept(null);
    }

    /**
     * Updated user
     * This can only be done by the logged in user.
     *
     * @param username name that need to be deleted (required)
     * @param userDto  Updated user object (optional)
     */
    @PUT("/user/{username}")
    public void updateUser(
            @PathParam("username") String username,
            @JsonBody UserDto userDto
    ) {
        User user = repository.query().username(username).single()
                .orElseThrow(() -> new EntityNotFoundException("User", username));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        repository.save(user);
    }
}
