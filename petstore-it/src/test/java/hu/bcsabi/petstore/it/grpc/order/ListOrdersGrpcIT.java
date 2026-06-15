package hu.bcsabi.petstore.it.grpc.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.bcsabi.petstore.order.grpc.ListOrdersRequest;
import hu.bcsabi.petstore.order.grpc.ListOrdersResponse;
import hu.bcsabi.petstore.order.grpc.Order;

class ListOrdersGrpcIT extends AbstractGrpcOrderIT {

    @Test
    void shouldListOrders() {
        // given
        createdOrderId = createOrderViaRest(existingPetId);
        ListOrdersRequest request = ListOrdersRequest.newBuilder().build();

        // when
        ListOrdersResponse response = orderStub.listOrders(request);

        // then
        assertThat(response.getOrdersList())
            .extracting(Order::getId)
            .contains(createdOrderId.toString());
    }

    @Test
    void shouldExcludeOrderWithoutShipDateWhenDateFilterIsGiven() {
        // given
        createdOrderId = createOrderViaRest(existingPetId);
        ListOrdersRequest request = ListOrdersRequest.newBuilder()
            .setShipDateFrom("2026-01-01")
            .setShipDateTo("2026-12-31")
            .build();

        // when
        ListOrdersResponse response = orderStub.listOrders(request);

        // then
        assertThat(response.getOrdersList())
            .isNotEmpty()
            .extracting(Order::getId)
            .doesNotContain(createdOrderId.toString());
    }

}
