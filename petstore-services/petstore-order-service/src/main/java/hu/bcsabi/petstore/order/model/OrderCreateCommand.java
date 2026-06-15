package hu.bcsabi.petstore.order.model;

import java.util.UUID;

/**
 * Data object for creating a new order.
 *
 * @param petId the id of the pet to order
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public record OrderCreateCommand(UUID petId) {
}
