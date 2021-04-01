package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.PetEntity;
import com.soprasteria.workshop.openapi.domain.PetStatus;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseSaveResult;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextJoinedSelectBuilder;
import org.fluentjdbc.DbContextTable;
import org.fluentjdbc.DbContextTableAlias;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("ALL")
public class PetRepository implements Repository<Pet> {

    private final DbContextTable table;
    private final DbContextTable tagsTable;
    private final DbContextTable urlsTable;
    private final CategoryRepository categoryRepository;
    public DbContextTableAlias tableAlias;
    public DbContextTableAlias cAlias;

    public PetRepository(DbContext context) {
        table = context.table("PETS");
        tagsTable = context.table("PETS_TAGS");
        urlsTable = context.table("PETS_URLS");
        categoryRepository = new CategoryRepository(context);
        tableAlias = table.alias("p");
        cAlias = categoryRepository.tableAlias("c");
    }

    @Override
    public DatabaseSaveResult.SaveStatus save(Pet o) {
        DatabaseSaveResult<UUID> result = table.newSaveBuilderWithUUID("id", o.getId())
                .setField("name", o.getName())
                .setField("status", o.getStatus())
                .setField("category_id", o.getCategoryId())
                .execute();
        o.setId(result.getId());
        return result.getSaveStatus();
    }

    @Override
    public Pet retrieve(UUID id) {
        return table.query().where("id", id)
                .singleObject(PetRepository::toPet)
                .orElseThrow(() -> new EntityNotFoundException(Pet.class, id));
    }

    public void delete(UUID petId) {
        table.query().where("id", petId).executeDelete();
    }

    @Override
    public PetQuery query() {
        return new PetQuery(tableAlias.select().query());
    }

    private static Pet toPet(DatabaseRow row) throws SQLException {
        Pet pet = new Pet();
        pet.setId(row.getUUID("id"));
        pet.setName(row.getString("name"));
        pet.setStatus(row.getEnum(PetStatus.class, "status"));
        pet.setCategoryId(row.getUUID("category_id"));
        return pet;
    }

    public void saveTags(Pet pet, List<String> tags) {
        tagsTable.where("pet_id", pet.getId()).executeDelete();
        for (String tag : tags) {
            tagsTable.newSaveBuilderWithUUID("id", null)
                    .uniqueKey("pet_id", pet.getId())
                    .uniqueKey("tag", tag)
                    .execute();
        }
    }

    public void saveUrls(Pet pet, List<String> urls) {
        for (String url : urls) { 
            urlsTable.newSaveBuilderWithUUID("id", null)
                    .uniqueKey("pet_id", pet.getId())
                    .uniqueKey("url", url)
                    .execute();
        }
    }

    public PetEntity retrieveEntity(UUID id) {
        return tableAlias.join(tableAlias.column("category_id"), cAlias.column("id"))
                .where("p.id", id)
                .singleObject(this::toEntity)
                .orElseThrow(() -> new EntityNotFoundException(Pet.class, id));
    }

    private PetEntity toEntity(DatabaseRow row) throws SQLException {
        return new PetEntity(
                toPet(row.table(tableAlias)),
                CategoryRepository.toCategory(row.table(cAlias)),
                listTags(row.table(tableAlias).getUUID("id")),
                listUrls(row.table(tableAlias).getUUID("id"))
        );
    }

    private List<String> listUrls(UUID id) {
        return urlsTable.query().where("pet_id", id).listStrings("url");
    }

    private List<String> listTags(UUID id) {
        return tagsTable.query().where("pet_id", id).listStrings("tag");
    }

    public class PetQuery implements Query<Pet> {

        private final DbContextJoinedSelectBuilder query;

        public PetQuery(DbContextJoinedSelectBuilder query) {
            this.query = query;
        }

        @Override
        public Stream<Pet> stream() {
            return query.stream(PetRepository::toPet);
        }

        public PetQuery status(Optional<Stream<PetStatus>> statuses) {
            statuses.ifPresent(this::status);
            return this;
        }

        private void status(Stream<PetStatus> statuses) {
            query.whereIn("status", statuses.collect(Collectors.toList()));
        }

        public Stream<PetEntity> streamEntities() {
            return query
                .join(tableAlias.column("category_id"), cAlias.column("id"))
                .stream(row -> toEntity(row));
        }
    }

}
