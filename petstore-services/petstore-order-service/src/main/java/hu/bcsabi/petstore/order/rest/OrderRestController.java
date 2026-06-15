package hu.bcsabi.petstore.order.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.json.JsonArray;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import hu.bcsabi.petstore.order.api.OrderApi;
import hu.bcsabi.petstore.order.dto.JsonPatchOperation;
import hu.bcsabi.petstore.order.dto.OrderCreateRequest;
import hu.bcsabi.petstore.order.dto.OrderListElementDto;
import hu.bcsabi.petstore.order.dto.OrderListResponse;
import hu.bcsabi.petstore.order.dto.OrderResponse;
import hu.bcsabi.petstore.order.model.OrderCreateCommand;
import hu.bcsabi.petstore.order.model.OrderModel;
import hu.bcsabi.petstore.order.model.OrderQuery;
import hu.bcsabi.petstore.order.rest.mapper.OrderRestMapper;
import hu.bcsabi.petstore.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * REST controller for orders.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@RestController
@RequiredArgsConstructor
public class OrderRestController implements OrderApi {

    private final OrderService orderService;
    private final OrderRestMapper orderRestMapper;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<OrderResponse> createOrder(OrderCreateRequest orderCreateRequest) {
        OrderCreateCommand command = orderRestMapper.toCommand(orderCreateRequest);
        OrderModel order = orderService.createOrder(command);
        return ResponseEntity.ok(orderRestMapper.toResponse(order));
    }

    @Override
    public ResponseEntity<Void> deleteOrder(UUID orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<OrderResponse> getOrderById(UUID orderId) {
        OrderModel order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderRestMapper.toResponse(order));
    }

    @Override
    public ResponseEntity<OrderListResponse> getOrders(LocalDate from, LocalDate to) {
        OrderQuery query = new OrderQuery(from, to);
        List<OrderModel> orders = orderService.getOrders(query);
        List<OrderListElementDto> elements = orders.stream().map(orderRestMapper::toListElement).toList();
        return ResponseEntity.ok(new OrderListResponse(elements));
    }

    @Override
    public ResponseEntity<OrderResponse> patchOrder(UUID orderId, List<JsonPatchOperation> jsonPatchOperation) {
        JsonArray patchOperations = objectMapper.convertValue(jsonPatchOperation, JsonArray.class);
        OrderModel order = orderService.patchOrder(orderId, patchOperations);
        return ResponseEntity.ok(orderRestMapper.toResponse(order));
    }

}
