package hu.bcsabi.petstore.order.grpc.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import org.springframework.grpc.server.service.GrpcService;

import hu.bcsabi.petstore.common.core.exception.BadRequestException;
import hu.bcsabi.petstore.order.grpc.GetOrderRequest;
import hu.bcsabi.petstore.order.grpc.GetOrderResponse;
import hu.bcsabi.petstore.order.grpc.ListOrdersRequest;
import hu.bcsabi.petstore.order.grpc.ListOrdersResponse;
import hu.bcsabi.petstore.order.grpc.OrderServiceGrpc;
import hu.bcsabi.petstore.order.grpc.mapper.OrderGrpcMapper;
import hu.bcsabi.petstore.order.model.OrderModel;
import hu.bcsabi.petstore.order.model.OrderQuery;
import hu.bcsabi.petstore.order.problem.OrderProblemType;
import hu.bcsabi.petstore.order.service.OrderService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

/**
 * gRPC entry point for orders.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@GrpcService
@RequiredArgsConstructor
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;
    private final OrderGrpcMapper orderGrpcMapper;

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<GetOrderResponse> responseObserver) {
        UUID orderId = parseOrderId(request.getOrderId());

        OrderModel order = orderService.getOrderById(orderId);

        responseObserver.onNext(orderGrpcMapper.toGetOrderResponse(order));
        responseObserver.onCompleted();
    }

    @Override
    public void listOrders(ListOrdersRequest request, StreamObserver<ListOrdersResponse> responseObserver) {
        OrderQuery query = orderGrpcMapper.toQuery(request);

        List<OrderModel> orders = orderService.getOrders(query);

        responseObserver.onNext(orderGrpcMapper.toListOrdersResponse(orders));
        responseObserver.onCompleted();
    }

    private UUID parseOrderId(String orderId) {
        try {
            return UUID.fromString(orderId);
        } catch (IllegalArgumentException exception) {
            String message = MessageFormat.format("Invalid order id: [{0}], expected a UUID", orderId);
            throw new BadRequestException(OrderProblemType.INVALID_ORDER_ID, message, exception);
        }
    }

}
