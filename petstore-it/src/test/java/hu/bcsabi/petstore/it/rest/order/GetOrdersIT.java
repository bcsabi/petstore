package hu.bcsabi.petstore.it.rest.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import hu.bcsabi.petstore.order.dto.JsonPatchOperation;
import hu.bcsabi.petstore.order.dto.OrderCreateRequest;
import hu.bcsabi.petstore.order.dto.OrderListElementDto;
import hu.bcsabi.petstore.order.dto.OrderListResponse;
import hu.bcsabi.petstore.order.dto.OrderResponse;
import hu.bcsabi.petstore.order.dto.OrderStatusDto;

class GetOrdersIT extends AbstractOrderIT {

    @Test
    void shouldGetOrders() {
        // given
        OrderCreateRequest request = new OrderCreateRequest(existingPetId);
        OrderResponse createOrderResponse = createOrder(request);
        assertThat(createOrderResponse).isNotNull();

        createdOrderId = createOrderResponse.getId();

        LocalDate shipDate = LocalDate.now();

        JsonPatchOperation addShipDateOperation = new JsonPatchOperation();
        addShipDateOperation.op(JsonPatchOperation.OpEnum.ADD);
        addShipDateOperation.path("/shipDate");
        addShipDateOperation.value(shipDate.toString());

        OrderResponse patchOrderResponse = patchOrder(createdOrderId, List.of(addShipDateOperation));
        assertThat(patchOrderResponse).isNotNull();

        // when
        OrderListResponse orderListResponse = getOrders(
            LocalDate.now().minusYears(1),
            LocalDate.now().plusYears(1)
        );

        // then
        assertThat(orderListResponse).isNotNull();
        assertThat(orderListResponse.getElements()).hasSizeGreaterThan(1);

        OrderListElementDto orderListElementDto = orderListResponse.getElements()
            .stream().filter(e -> e.getId().equals(createdOrderId))
            .findFirst()
            .orElse(null);
        assertThat(orderListElementDto).isNotNull();
        assertThat(orderListElementDto.getPetId()).isEqualTo(existingPetId);
        assertThat(orderListElementDto.getId()).isNotNull();
        assertThat(orderListElementDto.getStatus()).isEqualTo(OrderStatusDto.PLACED);
        assertThat(orderListElementDto.getShipDate()).isEqualTo(shipDate);
    }

}
