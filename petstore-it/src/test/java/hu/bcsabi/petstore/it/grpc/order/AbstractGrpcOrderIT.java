package hu.bcsabi.petstore.it.grpc.order;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.client.RestTestClient;

import hu.bcsabi.petstore.it.grpc.AbstractGrpcIT;
import hu.bcsabi.petstore.order.dto.OrderCreateRequest;
import hu.bcsabi.petstore.order.dto.OrderResponse;
import hu.bcsabi.petstore.order.grpc.OrderServiceGrpc;

import io.grpc.stub.MetadataUtils;

abstract class AbstractGrpcOrderIT extends AbstractGrpcIT {

    @Value("${test-data.existing-pet-id}")
    protected UUID existingPetId;

    protected OrderServiceGrpc.OrderServiceBlockingStub orderStub;

    private RestTestClient orderRestTestClient;

    protected UUID createdOrderId;

    @BeforeEach
    void beforeEach() {
        this.orderStub = OrderServiceGrpc.newBlockingStub(channel)
            .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(apiKeyMetadata()));

        this.orderRestTestClient = RestTestClient.bindToServer()
            .baseUrl(baseUrl + "/api/store/order")
            .defaultHeader(API_KEY_HEADER, apiKey)
            .build();
    }

    @AfterEach
    void afterEach() {
        if (createdOrderId != null) {
            orderRestTestClient.delete()
                .uri("/{orderId}", createdOrderId)
                .exchange()
                .expectStatus().isNoContent();
        }
    }

    protected UUID createOrderViaRest(UUID petId) {
        OrderResponse response = orderRestTestClient.post()
            .body(new OrderCreateRequest(petId))
            .exchange()
            .expectStatus().isOk()
            .expectBody(OrderResponse.class)
            .returnResult()
            .getResponseBody();

        return response.getId();
    }

}
