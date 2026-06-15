package hu.bcsabi.petstore.it.grpc.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import hu.bcsabi.petstore.order.grpc.GetOrderRequest;
import hu.bcsabi.petstore.order.grpc.GetOrderResponse;
import hu.bcsabi.petstore.order.grpc.Order;
import hu.bcsabi.petstore.order.grpc.OrderStatus;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

class GetOrderGrpcIT extends AbstractGrpcOrderIT {

    @Test
    void shouldGetOrderById() {
        // given
        createdOrderId = createOrderViaRest(existingPetId);
        GetOrderRequest request = GetOrderRequest.newBuilder()
            .setOrderId(createdOrderId.toString())
            .build();

        // when
        GetOrderResponse response = orderStub.getOrder(request);

        // then
        Order order = response.getOrder();
        assertThat(order.getId()).isEqualTo(createdOrderId.toString());
        assertThat(order.getPetId()).isEqualTo(existingPetId.toString());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER_STATUS_PLACED);
        assertThat(order.hasShipDate()).isFalse();
    }

    @Test
    void shouldThrowNotFoundWhenOrderDoesNotExist() {
        // given
        GetOrderRequest request = GetOrderRequest.newBuilder()
            .setOrderId(UUID.randomUUID().toString())
            .build();

        // when / then
        assertThatThrownBy(() -> orderStub.getOrder(request))
            .isInstanceOf(StatusRuntimeException.class)
            .extracting(exception -> ((StatusRuntimeException) exception).getStatus().getCode())
            .isEqualTo(Status.Code.NOT_FOUND);
    }

    @Test
    void shouldThrowInvalidArgumentWhenOrderIdIsNotUuid() {
        // given
        GetOrderRequest request = GetOrderRequest.newBuilder()
            .setOrderId("not-a-uuid")
            .build();

        // when / then
        assertThatThrownBy(() -> orderStub.getOrder(request))
            .isInstanceOf(StatusRuntimeException.class)
            .extracting(exception -> ((StatusRuntimeException) exception).getStatus().getCode())
            .isEqualTo(Status.Code.INVALID_ARGUMENT);
    }

}
