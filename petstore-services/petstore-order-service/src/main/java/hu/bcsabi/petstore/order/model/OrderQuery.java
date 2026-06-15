package hu.bcsabi.petstore.order.model;

import java.time.LocalDate;

/**
 * Filters for listing orders. A {@code null} field means no filter on it.
 *
 * @param shipDateFrom the ship date lower bound (optional)
 * @param shipDateTo   the ship date upper bound (optional)
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public record OrderQuery(LocalDate shipDateFrom, LocalDate shipDateTo) {
}
