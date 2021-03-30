package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.PetEntity;
import com.soprasteria.workshop.openapi.domain.PetStatus;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseSaveResult;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextSelectBuilder;
import org.fluentjdbc.DbContextTable;
import org.fluentjdbc.DbContextTableAlias;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PetRepository implements Repository<Pet> {

    private final DbContextTable table;
    private final DbContextTable tagsTable;
    private final DbContextTable urlsTable;
    private final CategoryRepository categoryRepository;

    public PetRepository(DbContext context) {
        table = context.table("PETS");
        tagsTable = context.table("PETS_TAGS");
        urlsTable = context.table("PETS_URLS");
        categoryRepository = new CategoryRepository(context);
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

    @Override
    public Query<Pet> query() {
        return new PetQuery(table.query());
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
        DbContextTableAlias p = table.alias("p");
        DbContextTableAlias c = categoryRepository.tableAlias("c");
        
        return p.join(p.column("category_id"), c.column("id"))
                .where("p.id", id)
                .singleObject(row -> new PetEntity(
                        toPet(row.table(p)),
                        CategoryRepository.toCategory(row.table(c)),
                        listTags(id),
                        listUrls(id)
        )).orElseThrow(() -> new EntityNotFoundException(Pet.class, id));
    }

    private List<String> listUrls(UUID id) {
        return urlsTable.query().where("pet_id", id).listStrings("url");
    }

    private List<String> listTags(UUID id) {
        return tagsTable.query().where("pet_id", id).listStrings("tag");
    }

    public static class PetQuery implements Query<Pet> {

        private final DbContextSelectBuilder query;

        public PetQuery(DbContextSelectBuilder query) {
            this.query = query;
        }

        @Override
        public List<Pet> list() {
            return query.list(PetRepository::toPet);
        }
    }

}
