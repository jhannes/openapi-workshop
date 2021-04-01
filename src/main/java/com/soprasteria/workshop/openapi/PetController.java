package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.PetEntity;
import com.soprasteria.workshop.openapi.domain.PetStatus;
import com.soprasteria.workshop.openapi.domain.repository.CategoryRepository;
import com.soprasteria.workshop.openapi.domain.repository.PetRepository;
import com.soprasteria.workshop.openapi.generated.petstore.CategoryDto;
import com.soprasteria.workshop.openapi.generated.petstore.PetDto;
import com.soprasteria.workshop.openapi.infrastructure.Multipart;
import org.actioncontroller.ContentBody;
import org.actioncontroller.ContentLocationHeader;
import org.actioncontroller.DELETE;
import org.actioncontroller.GET;
import org.actioncontroller.POST;
import org.actioncontroller.PUT;
import org.actioncontroller.PathParam;
import org.actioncontroller.RequestParam;
import org.actioncontroller.json.JsonBody;
import org.fluentjdbc.DbContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    public UUID addPet(@JsonBody PetDto petDto) {
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
    public void deletePet(@PathParam("petId") UUID petId) {
        petRepository.delete(petId);
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
    public Stream<PetDto> findPetsByStatus(@RequestParam("status") Optional<List<String>> status) {
        return petRepository.query()
                .status(status.map(list -> list.stream().map(PetDto.StatusEnum::fromValue).map(this::fromDto)))
                .streamEntities()
                .map(this::toDto);
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
    public List<PetDto> findPetsByTags(@RequestParam("tags") Optional<List<String>> tags) {
        // TODO
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
    public PetDto getPetById(@PathParam("petId") UUID petId) {
        return toDto(petRepository.retrieveEntity(petId));
    }

    /**
     * Update an existing pet
     *
     * @param petDto Pet object that needs to be added to the store (optional)
     */
    @PUT("/pet/{petId}")
    public void updatePet(@PathParam("petId") UUID petId, @JsonBody PetDto petDto) {
        Pet pet = fromDto(petDto);
        pet.setId(petId);
        petRepository.save(pet);
        petRepository.saveTags(pet, petDto.getTags());
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
            @RequestParam("status") Optional<String> status
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
            @Multipart.Filename("file") String fileName
    ) {
        Pet pet = petRepository.retrieve(petId);
        return petRepository.saveImage(pet, fileName, fileContent);
    }

    @GET("/pet/images/{fileId}")
    @ContentBody(contentType = "image/png")
    public byte[] getImage(@PathParam("fileId") UUID fileId) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        petRepository.readImage(fileId).transferTo(buffer);
        return buffer.toByteArray();
    }

    private Pet fromDto(PetDto petDto) {
        Pet pet = new Pet();
        pet.setName(petDto.getName());
        pet.setStatus(fromDto(petDto.getStatus()));
        pet.setCategoryId(petDto.getCategory().getId());
        return pet;
    }

    private PetDto toDto(PetEntity pet) {
        return new PetDto()
                .name(pet.getPet().getName())
                .id(pet.getPet().getId())
                .status(toDto(pet.getPet().getStatus()))
                .category(new CategoryDto().id(pet.getCategory().getId()).name(pet.getCategory().getName()))
                .tags(pet.getTags())
                .photoUrls(pet.getImages(), u -> "/pet/images/" + u);
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

