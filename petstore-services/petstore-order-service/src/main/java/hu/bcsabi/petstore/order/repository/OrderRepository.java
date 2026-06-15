package hu.bcsabi.petstore.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import hu.bcsabi.petstore.order.domain.Order;
import hu.bcsabi.petstore.order.model.OrderQuery;
import hu.bcsabi.petstore.order.repository.specifications.OrderSpecifications;

/**
 * Spring Data JPA repository for {@link Order} entities.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    /**
     * Returns all orders that match the filter.
     *
     * @param query the filter (optional)
     *
     * @return the matching orders
     */
    default List<Order> findAllByQuery(OrderQuery query) {
        return findAll(OrderSpecifications.fromQuery(query));
    }

}
