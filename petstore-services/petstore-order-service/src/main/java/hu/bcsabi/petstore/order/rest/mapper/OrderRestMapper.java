package hu.bcsabi.petstore.order.rest.mapper;

import org.mapstruct.Mapper;

import hu.bcsabi.petstore.order.dto.OrderCreateRequest;
import hu.bcsabi.petstore.order.dto.OrderListElementDto;
import hu.bcsabi.petstore.order.dto.OrderResponse;
import hu.bcsabi.petstore.order.model.OrderCreateCommand;
import hu.bcsabi.petstore.order.model.OrderModel;

/**
 * Mapper between REST DTOs and order model types.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Mapper(componentModel = "spring")
public interface OrderRestMapper {

    /**
     * Map OrderCreateRequest to OrderCreateCommand.
     *
     * @param request the REST request
     *
     * @return the create-order command
     */
    OrderCreateCommand toCommand(OrderCreateRequest request);

    /**
     * Map OrderModel to OrderResponse.
     *
     * @param model the order model
     *
     * @return the REST response
     */
    OrderResponse toResponse(OrderModel model);

    /**
     * Map OrderModel to OrderListElementDto.
     *
     * @param model the order model
     *
     * @return the list element
     */
    OrderListElementDto toListElement(OrderModel model);

}
