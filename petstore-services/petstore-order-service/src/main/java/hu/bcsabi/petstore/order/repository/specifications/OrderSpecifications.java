package hu.bcsabi.petstore.order.repository.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import hu.bcsabi.petstore.common.jpa.specification.CommonSpecifications;
import hu.bcsabi.petstore.order.domain.Order;
import hu.bcsabi.petstore.order.domain.Order_;
import hu.bcsabi.petstore.order.model.OrderQuery;

/**
 * Query specifications for orders.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    /**
     * Builds one specification from the filter.
     *
     * @param query the filter (optional)
     *
     * @return a specification matching all set filters; unrestricted if {@code query} is {@code null}
     */
    public static Specification<Order> fromQuery(OrderQuery query) {
        if (query == null) {
            return Specification.unrestricted();
        }
        return Specification.allOf(
            shipDateGreaterThanOrEqualTo(query.shipDateFrom()),
            shipDateLessThanOrEqualTo(query.shipDateTo())
        );
    }

    /**
     * Orders with ship date on or after the given value.
     *
     * @param from the ship date lower bound (optional)
     *
     * @return a specification; unrestricted if {@code from} is {@code null}
     */
    public static Specification<Order> shipDateGreaterThanOrEqualTo(LocalDate from) {
        return CommonSpecifications.greaterThanOrEqualTo(Order_.shipDate, from);
    }

    /**
     * Orders with ship date on or before the given value.
     *
     * @param to the ship date upper bound (optional)
     *
     * @return a specification; unrestricted if {@code to} is {@code null}
     */
    public static Specification<Order> shipDateLessThanOrEqualTo(LocalDate to) {
        return CommonSpecifications.lessThanOrEqualTo(Order_.shipDate, to);
    }

}
