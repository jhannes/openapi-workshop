package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Category;
import com.soprasteria.workshop.openapi.infrastructure.repository.EntityNotFoundException;
import com.soprasteria.workshop.openapi.infrastructure.repository.Query;
import com.soprasteria.workshop.openapi.infrastructure.repository.Repository;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseSaveResult;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextTable;
import org.fluentjdbc.DbContextTableAlias;

import java.sql.SQLException;
import java.util.UUID;

public class CategoryRepository implements Repository<Category> {

    private final DbContextTable table;

    public CategoryRepository(DbContext context) {
        table = context.table("categories");
    }

    @Override
    public DatabaseSaveResult.SaveStatus save(Category o) {
        DatabaseSaveResult<UUID> result = table.newSaveBuilderWithUUID("id", o.getId())
                .uniqueKey("name", o.getName())
                .execute();
        o.setId(result.getId());
        return result.getSaveStatus();
    }

    @Override
    public Query<Category> query() {
        return () -> table.query().stream(CategoryRepository::toCategory);
    }

    @Override
    public Category retrieve(UUID id) {
        return table.where("id", id).singleObject(CategoryRepository::toCategory)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, id));
    }

    @Override
    public void delete(Category o) {
        table.where("id", o.getId()).executeDelete();
    }

    static Category toCategory(DatabaseRow row) throws SQLException {
        Category category = new Category(row.getString("name"));
        category.setId(row.getUUID("id"));
        return category;
    }

    public DbContextTableAlias tableAlias(String alias) {
        return table.alias(alias);
    }

}
