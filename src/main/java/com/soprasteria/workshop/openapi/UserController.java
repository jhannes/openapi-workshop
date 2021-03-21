package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.generated.petstore.UserDto;
import org.actioncontroller.DELETE;
import org.actioncontroller.GET;
import org.actioncontroller.POST;
import org.actioncontroller.PUT;
import org.actioncontroller.PathParam;
import org.actioncontroller.RequestParam;
import org.actioncontroller.json.JsonBody;

import java.util.List;
import java.util.Optional;

public class UserController {
    /**
     * Create user
     * This can only be done by the logged in user.
     *
     * @param userDto Created user object (optional)
     */
    @POST("/user")
    public void createUser(
            @JsonBody UserDto userDto
    ) {

    }

    /**
     * Creates list of users with given input array
     *
     * @param userDto List of user object (optional
     */
    @POST("/user/createWithArray")
    public void createUsersWithArrayInput(
            @JsonBody List<UserDto> userDto
    ) {

    }

    /**
     * Creates list of users with given input array
     *
     * @param userDto List of user object (optional
     */
    @POST("/user/createWithList")
    public void createUsersWithListInput(
            @JsonBody List<UserDto> userDto
    ) {

    }

    /**
     * Delete user
     * This can only be done by the logged in user.
     *
     * @param username The name that needs to be deleted (required)
     */
    @DELETE("/user/{username}")
    public void deleteUser(
            @PathParam("username") String username
    ) {

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
        return null;
    }

    /**
     * Logs user into the system
     *
     * @param username The user name for login (optional)
     * @param password The password for login in clear text (optional)
     * @return String
     */
    @GET("/user/login")
    @JsonBody
    public String loginUser(
            @RequestParam("username") Optional<String> username,
            @RequestParam("password") Optional<String> password
    ) {
        return null;
    }

    /**
     * Logs out current logged in user session
     */
    @GET("/user/logout")
    public void logoutUser(
    ) {

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

    }
}
