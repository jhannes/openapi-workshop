package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.User;
import com.soprasteria.workshop.openapi.infrastructure.repository.EntityNotFoundException;
import com.soprasteria.workshop.openapi.infrastructure.repository.Query;
import com.soprasteria.workshop.openapi.infrastructure.repository.Repository;
import org.fluentjdbc.DatabaseResult;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseSaveResult;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextSelectBuilder;
import org.fluentjdbc.DbContextTable;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class UserRepository implements Repository<User> {

    private final DbContextTable table;

    public UserRepository(DbContext dbContext) {
        table = dbContext.tableWithTimestamps("users");
    }

    @Override
    public DatabaseSaveResult.SaveStatus save(User o) {
        DatabaseSaveResult<UUID> result = table.newSaveBuilderWithUUID("id", o.getId())
                .uniqueKey("username", o.getUsername())
                .setField("email", o.getEmail())
                .setField("first_name", o.getFirstName())
                .setField("last_name", o.getLastName())
                .setField("phone", o.getPhone())
                .execute();
        o.setId(result.getId());
        return result.getSaveStatus();
    }

    @Override
    public User retrieve(UUID id) {
        return table.where("id", id)
                .singleObject(this::toUser)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));
    }

    @Override
    public UserQuery query() {
        return new UserQuery(table.query());
    }

    @Override
    public void delete(User o) {

    }

    private User toUser(DatabaseRow row) throws SQLException {
        User user = new User();
        user.setId(row.getUUID("id"));
        user.setUsername(row.getString("username"));
        user.setEmail(row.getString("email"));
        user.setFirstName(row.getString("first_name"));
        user.setLastName(row.getString("last_name"));
        user.setPhone(row.getString("phone"));
        return user;
    }

    public class UserQuery implements Query<User> {
        private final DbContextSelectBuilder query;

        public UserQuery(DbContextSelectBuilder query) {
            this.query = query;
        }

        @Override
        public Stream<User> stream() {
            return query.stream(UserRepository.this::toUser);
        }

        public UserQuery username(String username) {
            query.where("username", username);
            return this;
        }

        public Optional<User> single() {
            return query.singleObject(UserRepository.this::toUser);
        }
    }
}
