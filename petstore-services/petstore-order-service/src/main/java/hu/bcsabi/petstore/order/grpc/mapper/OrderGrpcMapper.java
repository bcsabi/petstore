package hu.bcsabi.petstore.order.grpc.mapper;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Component;

import hu.bcsabi.petstore.common.core.exception.BadRequestException;
import hu.bcsabi.petstore.order.grpc.GetOrderResponse;
import hu.bcsabi.petstore.order.grpc.ListOrdersRequest;
import hu.bcsabi.petstore.order.grpc.ListOrdersResponse;
import hu.bcsabi.petstore.order.grpc.Order;
import hu.bcsabi.petstore.order.grpc.OrderStatus;
import hu.bcsabi.petstore.order.model.OrderModel;
import hu.bcsabi.petstore.order.model.OrderQuery;
import hu.bcsabi.petstore.order.problem.OrderProblemType;

/**
 * Mapper between gRPC proto types and order model types.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Component
public class OrderGrpcMapper {

    /**
     * Map OrderModel to a GetOrder response.
     *
     * @param model the order model
     *
     * @return the gRPC response
     */
    public GetOrderResponse toGetOrderResponse(OrderModel model) {
        return GetOrderResponse.newBuilder()
            .setOrder(toProto(model))
            .build();
    }

    /**
     * Map order models to a ListOrders response.
     *
     * @param models the order models
     *
     * @return the gRPC response
     */
    public ListOrdersResponse toListOrdersResponse(List<OrderModel> models) {
        return ListOrdersResponse.newBuilder()
            .addAllOrders(models.stream().map(this::toProto).toList())
            .build();
    }

    /**
     * Map a ListOrders request to an order query.
     *
     * @param request the gRPC request
     *
     * @return the order query
     */
    public OrderQuery toQuery(ListOrdersRequest request) {
        LocalDate from = request.hasShipDateFrom() ? parseDate(request.getShipDateFrom()) : null;
        LocalDate to = request.hasShipDateTo() ? parseDate(request.getShipDateTo()) : null;

        return new OrderQuery(from, to);
    }

    private Order toProto(OrderModel model) {
        Order.Builder builder = Order.newBuilder()
            .setId(model.id().toString())
            .setPetId(model.petId().toString())
            .setStatus(toProtoStatus(model.status()));

        if (model.shipDate() != null) {
            builder.setShipDate(model.shipDate().toString());
        }

        return builder.build();
    }

    private OrderStatus toProtoStatus(hu.bcsabi.petstore.order.domain.OrderStatus status) {
        return switch (status) {
            case PLACED -> OrderStatus.ORDER_STATUS_PLACED;
            case APPROVED -> OrderStatus.ORDER_STATUS_APPROVED;
            case DELIVERED -> OrderStatus.ORDER_STATUS_DELIVERED;
        };
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            String message = MessageFormat.format("Invalid date format: [{0}], expected ISO-8601 (yyyy-MM-dd)", value);
            throw new BadRequestException(OrderProblemType.INVALID_DATE_FORMAT, message, exception);
        }
    }

}
