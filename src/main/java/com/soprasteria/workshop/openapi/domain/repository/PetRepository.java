package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Pet;
import com.soprasteria.workshop.openapi.domain.PetEntity;
import com.soprasteria.workshop.openapi.domain.PetStatus;
import com.soprasteria.workshop.openapi.infrastructure.repository.EntityNotFoundException;
import com.soprasteria.workshop.openapi.infrastructure.repository.Query;
import com.soprasteria.workshop.openapi.infrastructure.repository.Repository;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseSaveResult;
import org.fluentjdbc.DatabaseStatement;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextJoinedSelectBuilder;
import org.fluentjdbc.DbContextTable;
import org.fluentjdbc.DbContextTableAlias;
import org.fluentjdbc.util.ExceptionUtil;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fluentjdbc.DatabaseStatement.parameterString;

@SuppressWarnings("ALL")
public class PetRepository implements Repository<Pet> {

    private final DbContextTable table;
    private final DbContextTable tagsTable;
    private final DbContextTable imagesTable;
    private final CategoryRepository categoryRepository;
    public DbContextTableAlias tableAlias;
    public DbContextTableAlias cAlias;

    public PetRepository(DbContext context) {
        table = context.table("PETS");
        tagsTable = context.table("PETS_TAGS");
        imagesTable = context.table("PETS_IMAGES");
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

    @Override
    public void delete(Pet pet) {
        table.query().where("id", pet.getId()).executeDelete();
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
                    .setField("pet_id", pet.getId())
                    .setField("tag", tag)
                    .execute();
        }
    }

    public UUID saveImage(Pet pet, String filename, InputStream fileContent) {
        UUID id = UUID.randomUUID();
        String sql = DatabaseStatement.createInsertSql(
                imagesTable.getTable().getTableName(), List.of("id", "pet_id", "filename", "content")
        );
        try (PreparedStatement statement = imagesTable.getConnection().prepareStatement(sql)) {
            statement.setObject(1, id);
            statement.setObject(2, pet.getId());
            statement.setString(3, filename);
            statement.setBinaryStream(4, fileContent);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw ExceptionUtil.softenCheckedException(e);
        }
        return id;
    }

    public InputStream readImage(UUID fileId) {
        return imagesTable.where("id", fileId)
                .singleObject(row -> ((Blob)row.getObject("content")).getBinaryStream())
                .orElseThrow(() -> new EntityNotFoundException("File", fileId));
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
                listImages(row.table(tableAlias).getUUID("id"))
        );
    }

    private List<UUID> listImages(UUID id) {
        return imagesTable.query().where("pet_id", id)
                .list(r -> r.getUUID("id"));
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

        public PetQuery tags(List<String> tags) {
            query.whereExpressionWithMultipleParameters(
                    "p.id in (select pet_id from pets_tags where tag in (" + parameterString(tags.size()) + "))",
                    tags
            );
            return this;
        }

        public Stream<PetEntity> streamEntities() {
            return query
                .join(tableAlias.column("category_id"), cAlias.column("id"))
                .stream(row -> toEntity(row));
        }
    }

}
