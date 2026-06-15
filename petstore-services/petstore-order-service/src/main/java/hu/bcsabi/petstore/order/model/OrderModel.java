package hu.bcsabi.petstore.order.model;

import java.time.LocalDate;
import java.util.UUID;

import hu.bcsabi.petstore.order.domain.OrderStatus;

/**
 * Order data returned by the service layer.
 *
 * @param id       the order id
 * @param petId    the ordered pet's id
 * @param shipDate the ship date, or {@code null} if not set
 * @param status   the order status
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public record OrderModel(UUID id, UUID petId, LocalDate shipDate, OrderStatus status) {
}
