package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.PetStatus;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseSaveResult;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextSelectBuilder;
import org.fluentjdbc.DbContextTable;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PetRepository implements Repository<Pet> {

    private DbContextTable table;

    public PetRepository(DbContext context) {
        table = context.table("PETS");
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
