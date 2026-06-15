package hu.bcsabi.petstore.it.rest.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.bcsabi.petstore.order.dto.OrderCreateRequest;
import hu.bcsabi.petstore.order.dto.OrderResponse;
import hu.bcsabi.petstore.order.dto.OrderStatusDto;

class CreateOrderIT extends AbstractOrderIT {

    @Test
    void shouldCreateOrder() {
        // given
        OrderCreateRequest request = new OrderCreateRequest(existingPetId);

        // when
        OrderResponse orderResponse = createOrder(request);

        // then
        assertThat(orderResponse).isNotNull();

        createdOrderId = orderResponse.getId();

        assertThat(orderResponse.getPetId()).isEqualTo(existingPetId);
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse.getStatus()).isEqualTo(OrderStatusDto.PLACED);
        assertThat(orderResponse.getShipDate()).isNull();
    }

}
