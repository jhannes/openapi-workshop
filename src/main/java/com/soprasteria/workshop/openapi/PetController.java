package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.generated.petstore.PetDto;
import org.actioncontroller.DELETE;
import org.actioncontroller.GET;
import org.actioncontroller.POST;
import org.actioncontroller.PUT;
import org.actioncontroller.PathParam;
import org.actioncontroller.RequestParam;
import org.actioncontroller.json.JsonBody;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class PetController {
    /**
     * Add a new pet to the store
     *
     * @param petDto Pet object that needs to be added to the store (optional)
     */
    @POST("/pet")
    public void addPet(
            @JsonBody PetDto petDto
    ) {

    }

    /**
     * Deletes a pet
     *
     * @param petId Pet id to delete (required)
     */
    @DELETE("/pet/{petId}")
    public void deletePet(
            @PathParam("petId") Long petId
    ) {

    }

    /**
     * Finds Pets by status
     * Multiple status values can be provided with comma separated strings
     *
     * @param status Status values that need to be considered for filter (optional
     * @return List&lt;PetDto&gt;
     */
    @GET("/pet/findByStatus")
    @JsonBody
    public List<PetDto> findPetsByStatus(
            @RequestParam("status") Optional<List<String>> status
    ) {
        return null;
    }

    /**
     * Finds Pets by tags
     * Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing.
     *
     * @param tags Tags to filter by (optional
     * @return List&lt;PetDto&gt;
     * @deprecated
     */
    @Deprecated
    @GET("/pet/findByTags")
    @JsonBody
    public List<PetDto> findPetsByTags(
            @RequestParam("tags") Optional<List<String>> tags
    ) {
        return null;
    }

    /**
     * Find pet by ID
     * Returns a pet when ID &lt; 10.  ID &gt; 10 or nonintegers will simulate API error conditions
     *
     * @param petId ID of pet that needs to be fetched (required)
     * @return PetDto
     */
    @GET("/pet/{petId}")
    @JsonBody
    public PetDto getPetById(
            @PathParam("petId") Long petId
    ) {
        return null;
    }

    /**
     * Update an existing pet
     *
     * @param petDto Pet object that needs to be added to the store (optional)
     */
    @PUT("/pet")
    public void updatePet(
            @JsonBody PetDto petDto
    ) {

    }

    /**
     * Updates a pet in the store with form data
     *
     * @param petId  ID of pet that needs to be updated (required)
     * @param name   Updated name of the pet (optional)
     * @param status Updated status of the pet (optional)
     */
    @POST("/pet/{petId}")
    public void updatePetWithForm(
            @PathParam("petId") String petId,
            @RequestParam("name") Optional<String> name,
            @RequestParam("status") Optional<String> status
    ) {

    }

    /**
     * uploads an image
     *
     * @param petId              ID of pet to update (required)
     * @param additionalMetadata Additional data to pass to server (optional)
     * @param file               file to upload (optional)
     */
    @POST("/pet/{petId}/uploadImage")
    public void uploadFile(
            @PathParam("petId") Long petId,
            @RequestParam("additionalMetadata") Optional<String> additionalMetadata,
            @RequestParam("file") Optional<File> file
    ) {

    }

}

