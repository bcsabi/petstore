package hu.bcsabi.petstore.order.service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import hu.bcsabi.petstore.common.core.exception.BadRequestException;
import hu.bcsabi.petstore.common.core.exception.BusinessException;
import hu.bcsabi.petstore.common.core.exception.BusinessObjectNotFoundException;
import hu.bcsabi.petstore.order.domain.Order;
import hu.bcsabi.petstore.order.domain.OrderStatus;
import hu.bcsabi.petstore.order.domain.Pet;
import hu.bcsabi.petstore.order.domain.PetStatus;
import hu.bcsabi.petstore.order.json.OrderJsonPatch;
import hu.bcsabi.petstore.order.mapper.OrderMapper;
import hu.bcsabi.petstore.order.model.OrderCreateCommand;
import hu.bcsabi.petstore.order.model.OrderModel;
import hu.bcsabi.petstore.order.model.OrderQuery;
import hu.bcsabi.petstore.order.problem.OrderProblemType;
import hu.bcsabi.petstore.order.repository.OrderRepository;
import hu.bcsabi.petstore.order.repository.PetRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;

/**
 * Business operations for managing orders.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PetRepository petRepository;
    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * Creates a new order with {@code PLACED} status for an available pet.
     * The pet's status is set to {@code PENDING} after the order is created.
     *
     * @param orderCreateCommand the create-order data
     *
     * @return the created order
     */
    @Transactional
    public OrderModel createOrder(OrderCreateCommand orderCreateCommand) {
        Assert.notNull(orderCreateCommand, "orderCreateCommand cannot be null");

        log.info("Creating order for pet: [{}]", orderCreateCommand.petId());

        Pet pet = findPetByIdOrThrowNotFound(orderCreateCommand.petId());

        if (pet.getStatus() != PetStatus.AVAILABLE) {
            String message = MessageFormat.format("Pet [{0}] is not available", pet.getId());
            throw new BusinessException(OrderProblemType.PET_NOT_AVAILABLE, message);
        }

        Order order = new Order();
        order.setPet(pet);
        order.setStatus(OrderStatus.PLACED);
        order = orderRepository.save(order);

        log.info("Order [{}] created for Pet [{}]", order.getId(), pet.getId());

        changePetStatus(pet, PetStatus.PENDING);

        return orderMapper.toModel(order);
    }

    /**
     * Returns the order with the given id.
     *
     * @param orderId the order id
     *
     * @return the matching order
     */
    @Transactional(readOnly = true)
    public OrderModel getOrderById(UUID orderId) {
        Assert.notNull(orderId, "orderId cannot be null");

        log.info("Getting order by id: [{}]", orderId);

        Order order = findOrderByIdOrThrowNotFound(orderId);

        return orderMapper.toModel(order);
    }

    /**
     * Returns all orders that match the filter.
     *
     * @param orderQuery the filter (optional)
     *
     * @return the matching orders
     */
    @Transactional(readOnly = true)
    public List<OrderModel> getOrders(OrderQuery orderQuery) {
        log.info("Getting orders by query: [{}]", orderQuery);

        validateOrderQuery(orderQuery);

        List<Order> orders = orderRepository.findAllByQuery(orderQuery);

        return orders.stream().map(orderMapper::toModel).toList();
    }

    /**
     * Deletes the order with the given id.
     *
     * @param orderId the order id
     */
    @Transactional
    public void deleteOrder(UUID orderId) {
        Assert.notNull(orderId, "orderId cannot be null");

        log.info("Deleting order by id: [{}]", orderId);

        Order order = findOrderByIdOrThrowNotFound(orderId);

        if (order.getStatus() != OrderStatus.PLACED) {
            String message = MessageFormat.format("Order [{0}] cannot be deleted because it is in [{1}] status", order.getId(), order.getStatus());
            throw new BusinessException(OrderProblemType.ORDER_CANNOT_BE_DELETED, message);
        }

        changePetStatus(order.getPet(), PetStatus.AVAILABLE);

        orderRepository.delete(order);

        log.info("Order [{}] deleted", order.getId());
    }

    /**
     * Applies an RFC 6902 JSON Patch to the order's shipDate and status.
     *
     * @param orderId         the order id
     * @param patchOperations the RFC 6902 operations
     *
     * @return the patched order
     */
    @Transactional
    public OrderModel patchOrder(UUID orderId, JsonArray patchOperations) {
        Assert.notNull(orderId, "orderId cannot be null");
        Assert.notNull(patchOperations, "patchOperations cannot be null");

        log.info("Patching order [{}]", orderId);

        Order order = findOrderByIdOrThrowNotFound(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            String message = MessageFormat.format("Order [{0}] is delivered and cannot be modified", order.getId());
            throw new BusinessException(OrderProblemType.ORDER_NOT_MODIFIABLE, message);
        }

        OrderJsonPatch sourceView = new OrderJsonPatch(order.getShipDate(), order.getStatus());
        OrderJsonPatch patchedView = applyJsonPatch(patchOperations, sourceView);

        log.debug("Patch applied successfully to view, sourceView: [{}] patchedView: [{}]", sourceView, patchedView);

        validatePatchedView(patchedView);
        validateTransition(sourceView.status(), patchedView.status());
        validateShipDate(sourceView, patchedView);

        log.debug("Validation passed, setting order [{}] fields by patchedView [{}]", order.getId(), patchedView);

        order.setShipDate(patchedView.shipDate());
        order.setStatus(patchedView.status());
        order = orderRepository.save(order);

        log.info("Order [{}] patched successfully", order.getId());

        Pet pet = order.getPet();

        if (order.getStatus() == OrderStatus.APPROVED && pet.getStatus() == PetStatus.PENDING) {
            changePetStatus(order.getPet(), PetStatus.SOLD);
        }

        return orderMapper.toModel(order);
    }

    private Pet findPetByIdOrThrowNotFound(UUID petId) {
        return petRepository.findById(petId)
            .orElseThrow(() -> new BusinessObjectNotFoundException(
                OrderProblemType.PET_NOT_FOUND,
                MessageFormat.format("Pet [{0}] not found", petId)
            ));
    }

    private Order findOrderByIdOrThrowNotFound(UUID orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessObjectNotFoundException(
                OrderProblemType.ORDER_NOT_FOUND,
                MessageFormat.format("Order [{0}] not found", orderId)
            ));
    }

    private void validateOrderQuery(OrderQuery orderQuery) {
        if (orderQuery == null) {
            log.debug("Order query is null, no validation required");
            return;
        }

        log.debug("Validating order query: [{}]", orderQuery);

        LocalDate from = orderQuery.shipDateFrom();
        LocalDate to = orderQuery.shipDateTo();

        if (from != null && to != null && from.isAfter(to)) {
            String message = MessageFormat.format("Invalid date range: [{0}] is after [{1}]", from, to);
            throw new BadRequestException(OrderProblemType.INVALID_DATE_RANGE, message);
        }

        log.debug("Order query is valid");
    }

    private OrderJsonPatch applyJsonPatch(JsonArray patchOperations, OrderJsonPatch currentView) {
        JsonObject currentDocument = objectMapper.convertValue(currentView, JsonObject.class);

        JsonObject patchedDocument;
        try {
            patchedDocument = Json.createPatch(patchOperations).apply(currentDocument);
        } catch (JsonException exception) {
            throw new BadRequestException(OrderProblemType.PATCH_NOT_APPLICABLE, exception.getMessage(), exception);
        }

        try {
            return objectMapper.readerFor(OrderJsonPatch.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(patchedDocument.toString());
        } catch (JacksonException exception) {
            throw new BadRequestException(OrderProblemType.INVALID_PATCH_CONTENT, exception.getMessage(), exception);
        }
    }

    private void validatePatchedView(OrderJsonPatch patchedView) {
        Set<ConstraintViolation<OrderJsonPatch>> violations = validator.validate(patchedView);
        if (!violations.isEmpty()) {
            throw new BadRequestException(OrderProblemType.INVALID_RESULTING_STATE, violations.toString());
        }
    }

    private void validateTransition(OrderStatus currentStatus, OrderStatus targetStatus) {
        if (!currentStatus.canTransitionTo(targetStatus)) {
            String message = MessageFormat.format("Cannot transition from [{0}] to [{1}]", currentStatus, targetStatus);
            throw new BusinessException(OrderProblemType.ILLEGAL_STATUS_TRANSITION, message);
        }
    }

    private void validateShipDate(OrderJsonPatch sourceView, OrderJsonPatch patchedView) {
        LocalDate shipDate = patchedView.shipDate();

        boolean shipDateChanged = !Objects.equals(sourceView.shipDate(), shipDate);
        if (shipDateChanged && shipDate != null && shipDate.isBefore(LocalDate.now())) {
            String message = MessageFormat.format("Ship date [{0}] must not be in the past", shipDate);
            throw new BadRequestException(OrderProblemType.SHIP_DATE_IN_PAST, message);
        }

        if (patchedView.status() != OrderStatus.PLACED && shipDate == null) {
            String message = MessageFormat.format("Ship date is required for status [{0}]", patchedView.status());
            throw new BusinessException(OrderProblemType.SHIP_DATE_REQUIRED, message);
        }
    }

    private void changePetStatus(Pet pet, PetStatus newStatus) {
        pet.setStatus(newStatus);
        petRepository.save(pet);
        log.info("Pet [{}] status changed to [{}]", pet.getId(), pet.getStatus());
    }

}
