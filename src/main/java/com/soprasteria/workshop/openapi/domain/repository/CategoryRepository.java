package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Category;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseSaveResult;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextSelectBuilder;
import org.fluentjdbc.DbContextTable;
import org.fluentjdbc.DbContextTableAlias;

import java.sql.SQLException;
import java.util.List;
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
    public CategoryQuery query() {
        return new CategoryQuery(table.query());
    }

    @Override
    public Category retrieve(UUID id) {
        return table.query().where("id", id).singleObject(CategoryRepository::toCategory)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, id));
    }

    static Category toCategory(DatabaseRow row) throws SQLException {
        Category category = new Category(row.getString("name"));
        category.setId(row.getUUID("id"));
        return category;
    }

    public DbContextTableAlias tableAlias(String alias) {
        return table.alias(alias);
    }

    public static class CategoryQuery implements Query<Category> {
        private final DbContextSelectBuilder query;

        public CategoryQuery(DbContextSelectBuilder query) {
            this.query = query;
        }

        @Override
        public List<Category> list() {
            return query.list(CategoryRepository::toCategory);
        }
    }
}
