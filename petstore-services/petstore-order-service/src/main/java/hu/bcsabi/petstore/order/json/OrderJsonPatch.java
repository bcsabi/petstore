package hu.bcsabi.petstore.order.json;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import hu.bcsabi.petstore.order.domain.OrderStatus;

/**
 * Patchable JSON view of an order.
 *
 * @param shipDate the requested ship date, or {@code null} if not set
 * @param status   the order status
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public record OrderJsonPatch(
    LocalDate shipDate,
    @NotNull @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES) OrderStatus status) {
}
