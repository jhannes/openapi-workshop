package com.soprasteria.workshop.openapi.controllers;

import com.soprasteria.workshop.openapi.domain.Order;
import com.soprasteria.workshop.openapi.domain.OrderStatus;
import com.soprasteria.workshop.openapi.domain.repository.OrderRepository;
import com.soprasteria.workshop.openapi.generated.petstore.OrderDto;
import org.actioncontroller.actions.DELETE;
import org.actioncontroller.actions.GET;
import org.actioncontroller.actions.POST;
import org.actioncontroller.values.ContentLocationHeader;
import org.actioncontroller.values.PathParam;
import org.actioncontroller.values.json.JsonBody;
import org.fluentjdbc.DbContext;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class StoreController {

    public OrderRepository repository;

    public StoreController(DbContext context) {
        repository = new OrderRepository(context);
    }

    /**
     * Place an order for a pet
     *
     * @param orderDto order placed for purchasing the pet (optional)
     * @return OrderDto
     */
    @POST("/store/order")
    @ContentLocationHeader("/store/order/{orderId}")
    public UUID placeOrder(@JsonBody OrderDto orderDto) {
        Order order = new Order();
        order.setComplete(Optional.ofNullable(orderDto.getComplete()).orElse(false));
        order.setPetId(orderDto.getPetId());
        order.setQuantity(orderDto.getQuantity());
        order.setShipDate(orderDto.getShipDate());
        order.setOrderStatus(fromDto(orderDto.getStatus()));
        repository.save(order);
        return order.getId();
    }

    /**
     * Find purchase order by ID
     *
     * @param orderId ID of pet that needs to be fetched (required)
     * @return OrderDto
     */
    @GET("/store/order/{orderId}")
    @JsonBody
    public OrderDto getOrderById(@PathParam("orderId") UUID orderId) {
        Order order = repository.retrieve(orderId);
        return new OrderDto().id(order.getId())
                .complete(order.isComplete())
                .petId(order.getPetId())
                .quantity(order.getQuantity())
                .shipDate(order.getShipDate())
                .status(toDto(order.getOrderStatus()));
    }

    /**
     * Delete purchase order by ID
     *
     * @param orderId ID of the order that needs to be deleted (required)
     */
    @DELETE("/store/order/{orderId}")
    public void deleteOrder(@PathParam("orderId") UUID orderId) {
        Order order = repository.retrieve(orderId);
        repository.delete(order);
    }

    /**
     * Returns pet inventories by status
     * Returns a map of status codes to quantities
     *
     * @return Map&lt;String, Integer&gt;
     */
    @GET("/store/inventory")
    @JsonBody
    public Map<String, Integer> getInventory(
    ) {
        return null;
    }

    private OrderStatus fromDto(OrderDto.StatusEnum status) {
        if (status == null) {
            return OrderStatus.PLACED;
        }
        switch (status) {
            case PLACED: return OrderStatus.PLACED;
            case APPROVED: return OrderStatus.APPROVED;
            case DELIVERED: return OrderStatus.DELIVERED;
            default: return null;
        }
    }

    private OrderDto.StatusEnum toDto(OrderStatus orderStatus) {
        switch (orderStatus) {
            case PLACED: return OrderDto.StatusEnum.PLACED;
            case APPROVED: return OrderDto.StatusEnum.APPROVED;
            case DELIVERED: return OrderDto.StatusEnum.DELIVERED;
            default: return null;
        }
    }
}
