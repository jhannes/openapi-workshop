package com.soprasteria.workshop.openapi.domain.repository;

import com.soprasteria.workshop.openapi.domain.Order;
import com.soprasteria.workshop.openapi.domain.OrderStatus;
import com.soprasteria.workshop.openapi.infrastructure.repository.EntityNotFoundException;
import com.soprasteria.workshop.openapi.infrastructure.repository.Query;
import com.soprasteria.workshop.openapi.infrastructure.repository.Repository;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseSaveResult;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextTable;

import java.sql.SQLException;
import java.util.UUID;

public class OrderRepository implements Repository<Order> {

    public DbContextTable table;

    public OrderRepository(DbContext dbContext) {
        table = dbContext.tableWithTimestamps("orders");
    }

    @Override
    public DatabaseSaveResult.SaveStatus save(Order o) {
        DatabaseSaveResult<UUID> result = table.newSaveBuilderWithUUID("id", o.getId())
                .setField("pet_id", o.getPetId())
                .setField("ship_date", o.getShipDate())
                .setField("is_complete", o.isComplete())
                .setField("quantity", o.getQuantity())
                .setField("order_status", o.getOrderStatus())
                .execute();
        o.setId(result.getId());
        return result.getSaveStatus();
    }

    @Override
    public Query<Order> query() {
        return null;
    }

    @Override
    public Order retrieve(UUID id) {
        return table.where("id", id).singleObject(this::toOrder)
                .orElseThrow(() -> new EntityNotFoundException(Order.class, id));
    }

    private Order toOrder(DatabaseRow row) throws SQLException {
        Order order = new Order();
        order.setId(row.getUUID("id"));
        order.setPetId(row.getUUID("pet_id"));
        order.setShipDate(row.getOffsetDateTime("ship_date"));
        order.setQuantity(row.getInt("quantity"));
        order.setComplete(row.getBoolean("is_complete"));
        order.setOrderStatus(row.getEnum(OrderStatus.class,"order_status"));
        return order;
    }
    
    @Override
    public void delete(Order order) {
        table.where("id", order.getId()).executeDelete();
    }
}
