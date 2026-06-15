package hu.bcsabi.petstore.it.rest.order;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import hu.bcsabi.petstore.it.AbstractIT;
import hu.bcsabi.petstore.order.dto.JsonPatchOperation;
import hu.bcsabi.petstore.order.dto.OrderCreateRequest;
import hu.bcsabi.petstore.order.dto.OrderListResponse;
import hu.bcsabi.petstore.order.dto.OrderResponse;

abstract class AbstractOrderIT extends AbstractIT {

    @Value("${test-data.existing-pet-id}")
    protected UUID existingPetId;

    protected RestTestClient orderRestTestClient;

    protected UUID createdOrderId;

    @BeforeEach
    void beforeEach() {
        this.orderRestTestClient = RestTestClient.bindToServer()
            .baseUrl(baseUrl + "/api/store/order")
            .defaultHeader(API_KEY_HEADER, apiKey)
            .build();
    }

    @AfterEach
    void afterEach() {
        if (createdOrderId != null) {
            deleteOrder(createdOrderId);
        }
    }

    protected OrderResponse createOrder(OrderCreateRequest createRequest) {
        return orderRestTestClient.post()
            .body(createRequest)
            .exchange()
            .expectStatus().isOk()
            .expectBody(OrderResponse.class)
            .returnResult()
            .getResponseBody();
    }

    protected void deleteOrder(UUID orderId) {
        orderRestTestClient.delete()
            .uri("/{orderId}", orderId)
            .exchange()
            .expectStatus().isNoContent();
    }

    protected OrderResponse getOrderById(UUID orderId) {
        return orderRestTestClient.get()
            .uri("/{orderId}", orderId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(OrderResponse.class)
            .returnResult()
            .getResponseBody();
    }

    protected OrderListResponse getOrders(LocalDate from, LocalDate to) {
        return orderRestTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .queryParam("from", from)
                .queryParam("to", to)
                .build()
            )
            .exchange()
            .expectStatus().isOk()
            .expectBody(OrderListResponse.class)
            .returnResult()
            .getResponseBody();
    }

    protected OrderResponse patchOrder(UUID orderId, List<JsonPatchOperation> jsonPatchOperation) {
        return orderRestTestClient.patch()
            .uri("/{orderId}", orderId)
            .contentType(MediaType.valueOf("application/json-patch+json"))
            .body(jsonPatchOperation)
            .exchange()
            .expectStatus().isOk()
            .expectBody(OrderResponse.class)
            .returnResult()
            .getResponseBody();
    }

}
