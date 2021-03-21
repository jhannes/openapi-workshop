package com.soprasteria.workshop.openapi;

import com.soprasteria.workshop.openapi.generated.petstore.OrderDto;
import org.actioncontroller.DELETE;
import org.actioncontroller.GET;
import org.actioncontroller.POST;
import org.actioncontroller.PathParam;
import org.actioncontroller.json.JsonBody;

import java.util.Map;

public class StoreController {
    /**
     * Delete purchase order by ID
     * For valid response try integer IDs with value &lt; 1000. Anything above 1000 or nonintegers will generate API errors
     *
     * @param orderId ID of the order that needs to be deleted (required)
     */
    @DELETE("/store/order/{orderId}")
    public void deleteOrder(
            @PathParam("orderId") String orderId
    ) {

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

    /**
     * Find purchase order by ID
     * For valid response try integer IDs with value &lt;&#x3D; 5 or &gt; 10. Other values will generated exceptions
     *
     * @param orderId ID of pet that needs to be fetched (required)
     * @return OrderDto
     */
    @GET("/store/order/{orderId}")
    @JsonBody
    public OrderDto getOrderById(
            @PathParam("orderId") String orderId
    ) {
        return null;
    }

    /**
     * Place an order for a pet
     *
     * @param orderDto order placed for purchasing the pet (optional)
     * @return OrderDto
     */
    @POST("/store/order")
    @JsonBody
    public OrderDto placeOrder(
            @JsonBody OrderDto orderDto
    ) {
        return null;
    }

}
