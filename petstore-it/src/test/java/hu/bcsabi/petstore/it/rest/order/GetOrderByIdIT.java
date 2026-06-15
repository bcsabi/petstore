package hu.bcsabi.petstore.it.rest.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.bcsabi.petstore.order.dto.OrderCreateRequest;
import hu.bcsabi.petstore.order.dto.OrderResponse;
import hu.bcsabi.petstore.order.dto.OrderStatusDto;

class GetOrderByIdIT extends AbstractOrderIT {

    @Test
    void shouldGetOrderById() {
        // given
        OrderCreateRequest request = new OrderCreateRequest(existingPetId);
        OrderResponse createOrderResponse = createOrder(request);
        assertThat(createOrderResponse).isNotNull();

        createdOrderId = createOrderResponse.getId();

        // when
        OrderResponse orderResponse = getOrderById(createdOrderId);

        // then
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getPetId()).isEqualTo(existingPetId);
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse.getStatus()).isEqualTo(OrderStatusDto.PLACED);
        assertThat(orderResponse.getShipDate()).isNull();
    }

}
