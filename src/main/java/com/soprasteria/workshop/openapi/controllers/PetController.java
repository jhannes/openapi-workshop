package com.soprasteria.workshop.openapi.controllers;

import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.PetEntity;
import com.soprasteria.workshop.openapi.domain.PetStatus;
import com.soprasteria.workshop.openapi.domain.repository.CategoryRepository;
import com.soprasteria.workshop.openapi.domain.repository.PetRepository;
import com.soprasteria.workshop.openapi.generated.petstore.CategoryDto;
import com.soprasteria.workshop.openapi.generated.petstore.PetDto;
import com.soprasteria.workshop.openapi.infrastructure.servlet.Multipart;
import com.soprasteria.workshop.openapi.infrastructure.servlet.PetStoreUser;
import org.actioncontroller.actions.DELETE;
import org.actioncontroller.actions.GET;
import org.actioncontroller.actions.POST;
import org.actioncontroller.actions.PUT;
import org.actioncontroller.exceptions.HttpRequestException;
import org.actioncontroller.values.ContentBody;
import org.actioncontroller.values.ContentLocationHeader;
import org.actioncontroller.values.PathParam;
import org.actioncontroller.values.RequestParam;
import org.actioncontroller.values.ServletUrl;
import org.actioncontroller.values.UserPrincipal;
import org.actioncontroller.values.json.JsonBody;
import org.fluentjdbc.DbContext;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class PetController {

    public CategoryRepository categoryRepository;
    public PetRepository petRepository;

    public PetController(DbContext context) {
        petRepository = new PetRepository(context);
        categoryRepository = new CategoryRepository(context);
    }

    /**
     * Returns valid pet categories
     * Returns a list of ids and names of categories
     * @return List&lt;CategoryDto&gt;
     */
    @GET("/category")
    @JsonBody
    public Stream<CategoryDto> listCategories() {
        return categoryRepository.query().stream()
                .map(c -> new CategoryDto().id(c.getId()).name(c.getName()));
    }

    /**
     * Add a new pet to the store
     *
     * @param petDto Pet object that needs to be added to the store (optional)
     */
    @POST("/pet")
    @ContentLocationHeader("/pet/{petId}")
    public UUID addPet(
            @JsonBody PetDto petDto,
            @UserPrincipal PetStoreUser ignoredUser
    ) {
        if (!petDto.missingRequiredFields().isEmpty()) {
            throw new HttpRequestException("Missing required fields " + petDto.missingRequiredFields());
        }

        Pet pet = fromDto(petDto);
        petRepository.save(pet);
        petRepository.saveTags(pet, petDto.getTags());
        return pet.getId();
    }

    /**
     * Deletes a pet
     *
     * @param petId Pet id to delete (required)
     */
    @DELETE("/pet/{petId}")
    public void deletePet(@PathParam("petId") UUID petId, @UserPrincipal PetStoreUser ignoredUser) {
        petRepository.delete(petRepository.retrieve(petId));
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
    public Stream<PetDto> findPetsByStatus(
            @RequestParam("status") Optional<List<PetDto.StatusEnum>> status,
            @ServletUrl String servletUrl
    ) {
        return petRepository.query()
                .status(status.map(list -> list.stream().map(this::fromDto)))
                .streamEntities()
                .map(pet -> toDto(pet, servletUrl));
    }

    /**
     * Finds Pets by tags
     * Multiple tags can be provided with comma separated strings. Use tag1, tag2, tag3 for testing.
     *
     * @param tags Tags to filter by (optional
     * @return List&lt;PetDto&gt;
     */
    @GET("/pet/findByTags")
    @JsonBody
    public Stream<PetDto> findPetsByTags(
            @RequestParam("tags") List<String> tags,
            @ServletUrl String servletUrl
    ) {
        return petRepository.query()
                .tags(tags)
                .streamEntities()
                .map(pet -> toDto(pet, servletUrl));
    }

    /**
     * Find pet by ID
     *
     * @param petId ID of pet that needs to be fetched (required)
     * @return PetDto
     */
    @GET("/pet/{petId}")
    @JsonBody
    public PetDto getPetById(@PathParam("petId") UUID petId, @ServletUrl String servletUrl) {
        return toDto(petRepository.retrieveEntity(petId), servletUrl);
    }

    /**
     * Update an existing pet
     *
     * @param petDto Pet object that needs to be added to the store (optional)
     */
    @PUT("/pet/{petId}")
    public void updatePet(
            @PathParam("petId") UUID petId,
            @JsonBody PetDto petDto,
            @UserPrincipal PetStoreUser ignoredUser
    ) {
        Pet pet = petRepository.retrieve(petId);
        pet.setName(petDto.getName());
        pet.setStatus(fromDto(petDto.getStatus()));
        if (petDto.getCategory() != null) {
            pet.setCategoryId(petDto.getCategory().getId());
        }
        petRepository.save(pet);
        if (petDto.getTags() != null && !petDto.getTags().isEmpty()) {
            petRepository.saveTags(pet, petDto.getTags());
        }
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
            @PathParam("petId") UUID petId,
            @RequestParam("name") Optional<String> name,
            @RequestParam("status") Optional<String> status,
            @UserPrincipal PetStoreUser ignoredUser
    ) {
        Pet pet = petRepository.retrieve(petId);
        name.ifPresent(pet::setName);
        status.map(PetDto.StatusEnum::fromValue).map(this::fromDto)
                .ifPresent(pet::setStatus);
        petRepository.save(pet);
    }

    /**
     * uploads an image
     *
     * @param petId              ID of pet to update (required)
     */
    @POST("/pet/{petId}/uploadImage")
    @ContentLocationHeader("/pet/images/{fileId}")
    public UUID uploadFile(
            @PathParam("petId") UUID petId,
            @Multipart("file") InputStream fileContent,
            @Multipart.Filename("file") String fileName,
            @UserPrincipal PetStoreUser ignoredUser

    ) {
        Pet pet = petRepository.retrieve(petId);
        return petRepository.saveImage(pet, fileName, fileContent);
    }

    @GET("/pet/images/{fileId}")
    @ContentBody(contentType = "image/png")
    public InputStream getImage(@PathParam("fileId") UUID fileId) {
        return petRepository.readImage(fileId);
    }

    private Pet fromDto(PetDto petDto) {
        Pet pet = new Pet();
        pet.setName(petDto.getName());
        pet.setStatus(fromDto(petDto.getStatus()));
        pet.setCategoryId(petDto.getCategory() != null ? petDto.getCategory().getId() : null);
        return pet;
    }

    private PetDto toDto(PetEntity pet, String servletUrl) {
        return new PetDto()
                .name(pet.getPet().getName())
                .id(pet.getPet().getId())
                .status(toDto(pet.getPet().getStatus()))
                .category(new CategoryDto().id(pet.getCategory().getId()).name(pet.getCategory().getName()))
                .tags(pet.getTags())
                .photoUrls(pet.getImages(), u -> servletUrl + "/pet/images/" + u);
    }

    private PetStatus fromDto(PetDto.StatusEnum status) {
        if (status == PetDto.StatusEnum.AVAILABLE) {
            return PetStatus.AVAILABLE;
        } else if (status == PetDto.StatusEnum.SOLD) {
            return PetStatus.SOLD;
        } else if (status == PetDto.StatusEnum.PENDING) {
            return PetStatus.PENDING;
        } else {
            return null;
        }
    }

    private PetDto.StatusEnum toDto(PetStatus status) {
        if (status == PetStatus.AVAILABLE) {
            return PetDto.StatusEnum.AVAILABLE;
        } else if (status == PetStatus.SOLD) {
            return PetDto.StatusEnum.SOLD;
        } else if (status == PetStatus.PENDING) {
            return PetDto.StatusEnum.PENDING;
        } else {
            return null;
        }
    }
}

