package hu.bcsabi.petstore.it.grpc.security;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import hu.bcsabi.petstore.it.grpc.AbstractGrpcIT;
import hu.bcsabi.petstore.order.grpc.GetOrderRequest;
import hu.bcsabi.petstore.order.grpc.OrderServiceGrpc;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;

/**
 * Verifies the x-api-key enforcement on the gRPC endpoints, mirroring the REST {@code SecurityIT}.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
class SecurityGrpcIT extends AbstractGrpcIT {

    private static final GetOrderRequest ANY_REQUEST = GetOrderRequest.newBuilder()
        .setOrderId(UUID.randomUUID().toString())
        .build();

    @Test
    void shouldThrowUnauthenticatedWhenApiKeyIsMissing() {
        // given
        OrderServiceGrpc.OrderServiceBlockingStub stubWithoutKey = OrderServiceGrpc.newBlockingStub(channel);

        // when / then
        assertThatThrownBy(() -> stubWithoutKey.getOrder(ANY_REQUEST))
            .isInstanceOf(StatusRuntimeException.class)
            .extracting(exception -> ((StatusRuntimeException) exception).getStatus().getCode())
            .isEqualTo(Status.Code.UNAUTHENTICATED);
    }

    @Test
    void shouldThrowUnauthenticatedWhenApiKeyIsInvalid() {
        // given
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of(API_KEY_HEADER, Metadata.ASCII_STRING_MARSHALLER), "definitely-wrong-key");
        OrderServiceGrpc.OrderServiceBlockingStub stubWithWrongKey = OrderServiceGrpc.newBlockingStub(channel)
            .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));

        // when / then
        assertThatThrownBy(() -> stubWithWrongKey.getOrder(ANY_REQUEST))
            .isInstanceOf(StatusRuntimeException.class)
            .extracting(exception -> ((StatusRuntimeException) exception).getStatus().getCode())
            .isEqualTo(Status.Code.UNAUTHENTICATED);
    }

}
