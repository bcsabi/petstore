package hu.bcsabi.petstore.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.bcsabi.petstore.order.domain.Order;
import hu.bcsabi.petstore.order.model.OrderModel;

/**
 * Mapper for the {@link Order} entity.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Map Order to OrderModel.
     *
     * @param order the order to map
     *
     * @return the order model
     */
    @Mapping(target = "petId", source = "pet.id")
    OrderModel toModel(Order order);

}
