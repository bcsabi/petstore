package hu.bcsabi.petstore.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import hu.bcsabi.petstore.common.core.exception.BadRequestException;
import hu.bcsabi.petstore.common.core.exception.BaseException;
import hu.bcsabi.petstore.common.core.exception.BusinessException;
import hu.bcsabi.petstore.common.core.exception.BusinessObjectNotFoundException;
import hu.bcsabi.petstore.order.domain.Order;
import hu.bcsabi.petstore.order.domain.OrderStatus;
import hu.bcsabi.petstore.order.domain.Pet;
import hu.bcsabi.petstore.order.domain.PetStatus;
import hu.bcsabi.petstore.order.mapper.OrderMapperImpl;
import hu.bcsabi.petstore.order.model.OrderCreateCommand;
import hu.bcsabi.petstore.order.model.OrderModel;
import hu.bcsabi.petstore.order.model.OrderQuery;
import hu.bcsabi.petstore.order.problem.OrderProblemType;
import hu.bcsabi.petstore.order.repository.OrderRepository;
import hu.bcsabi.petstore.order.repository.PetRepository;

/**
 * Unit tests for {@link OrderService}.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@SpringBootTest(classes = { OrderService.class, OrderMapperImpl.class })
@ImportAutoConfiguration({ JacksonAutoConfiguration.class, ValidationAutoConfiguration.class })
class OrderServiceTest {

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private PetRepository petRepository;

    @Autowired
    private OrderService orderService;

    @Nested
    class CreateOrderTest {

        @Test
        void shouldBeSuccessfulWhenPetIsAvailable() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.AVAILABLE);

            when(petRepository.findById(petId)).thenReturn(Optional.of(pet));

            when(orderRepository.save(any(Order.class)))
                .thenAnswer(answer -> {
                    Order order = answer.getArgument(0);
                    order.setId(UUID.randomUUID());
                    return order;
                });

            // when
            OrderModel result = orderService.createOrder(new OrderCreateCommand(petId));

            // then
            assertThat(result.id()).isNotNull();
            assertThat(result.petId()).isEqualTo(petId);
            assertThat(result.status()).isEqualTo(OrderStatus.PLACED);
            assertThat(result.shipDate()).isNull();
        }

        @Test
        void shouldThrowBusinessObjectNotFoundExceptionWhenPetDoesNotExist() {
            // given
            UUID petId = UUID.randomUUID();
            OrderCreateCommand command = new OrderCreateCommand(petId);

            when(petRepository.findById(petId)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> orderService.createOrder(command))
                .hasMessage("Pet [" + petId + "] not found")
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessObjectNotFoundException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.PET_NOT_FOUND);
        }

        @ParameterizedTest
        @EnumSource(value = PetStatus.class, names = { "AVAILABLE" }, mode = EnumSource.Mode.EXCLUDE)
        void shouldThrowBusinessExceptionWhenPetIsNotAvailable(PetStatus status) {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, status);

            OrderCreateCommand command = new OrderCreateCommand(petId);

            when(petRepository.findById(petId)).thenReturn(Optional.of(pet));

            // when / then
            assertThatThrownBy(() -> orderService.createOrder(command))
                .hasMessage("Pet [" + petId + "] is not available")
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.PET_NOT_AVAILABLE);
        }
    }

    @Nested
    class GetOrderByIdTest {

        @Test
        void shouldBeSuccessfulWhenOrderExists() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when
            OrderModel result = orderService.getOrderById(orderId);

            // then
            assertThat(result.id()).isEqualTo(orderId);
            assertThat(result.petId()).isEqualTo(petId);
            assertThat(result.status()).isEqualTo(OrderStatus.PLACED);
        }

        @Test
        void shouldThrowBusinessObjectNotFoundExceptionWhenOrderDoesNotExist() {
            // given
            UUID orderId = UUID.randomUUID();

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .hasMessage("Order [" + orderId + "] not found")
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessObjectNotFoundException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.ORDER_NOT_FOUND);
        }
    }

    @Nested
    class GetOrdersTest {

        @Test
        void shouldBeSuccessfulWhenQueryHasValidRange() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            LocalDate from = LocalDate.of(2026, 1, 1);
            LocalDate to = LocalDate.of(2026, 12, 31);
            OrderQuery query = new OrderQuery(from, to);

            when(orderRepository.findAllByQuery(query)).thenReturn(List.of(order));

            // when
            List<OrderModel> result = orderService.getOrders(query);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().id()).isEqualTo(orderId);
        }

        @Test
        void shouldBeSuccessfulWhenQueryIsNull() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            when(orderRepository.findAllByQuery(null)).thenReturn(List.of(order));

            // when
            List<OrderModel> result = orderService.getOrders(null);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().id()).isEqualTo(orderId);
        }

        @Test
        void shouldThrowBadRequestExceptionWhenDateRangeIsInvalid() {
            // given
            LocalDate from = LocalDate.of(2026, 12, 31);
            LocalDate to = LocalDate.of(2026, 1, 1);
            OrderQuery query = new OrderQuery(from, to);

            // when / then
            assertThatThrownBy(() -> orderService.getOrders(query))
                .asInstanceOf(InstanceOfAssertFactories.type(BadRequestException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.INVALID_DATE_RANGE);
        }
    }

    @Nested
    class DeleteOrderTest {

        @Test
        void shouldBeSuccessfulWhenOrderIsPlaced() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when
            orderService.deleteOrder(orderId);

            // then
            verify(orderRepository).delete(order);
            assertThat(pet.getStatus()).isEqualTo(PetStatus.AVAILABLE);
        }

        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = { "PLACED" }, mode = EnumSource.Mode.EXCLUDE)
        void shouldThrowBusinessExceptionWhenOrderIsNotPlaced(OrderStatus orderStatus) {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.SOLD);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, orderStatus);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when / then
            assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.ORDER_CANNOT_BE_DELETED);
        }

        @Test
        void shouldThrowBusinessObjectNotFoundExceptionWhenOrderDoesNotExist() {
            // given
            UUID orderId = UUID.randomUUID();

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .hasMessage("Order [" + orderId + "] not found")
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessObjectNotFoundException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.ORDER_NOT_FOUND);
        }
    }

    @Nested
    class PatchOrderTest {

        @Test
        void shouldBeSuccessfulWhenStatusChangeToApproved() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            LocalDate shipDate = LocalDate.now().plusDays(2);

            JsonArray patch = createPatch(
                createPatchOperation("replace", "/status", "approved"),
                createPatchOperation("add", "/shipDate", shipDate.toString())
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // when
            OrderModel result = orderService.patchOrder(orderId, patch);

            // then
            assertThat(result.status()).isEqualTo(OrderStatus.APPROVED);
            assertThat(result.shipDate()).isEqualTo(shipDate);
            assertThat(pet.getStatus()).isEqualTo(PetStatus.SOLD);
        }

        @Test
        void shouldBeSuccessfulWhenStatusChangeToDelivered() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.SOLD);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.APPROVED);
            order.setShipDate(LocalDate.now().plusDays(2));

            JsonArray patch = createPatch(
                createPatchOperation("replace", "/status", "delivered")
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // when
            OrderModel result = orderService.patchOrder(orderId, patch);

            // then
            assertThat(result.status()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        void shouldBeSuccessfulWhenShipDateIsAdded() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            LocalDate shipDate = LocalDate.now().plusDays(2);

            JsonArray patch = createPatch(
                createPatchOperation("add", "/shipDate", shipDate.toString())
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // when
            OrderModel result = orderService.patchOrder(orderId, patch);

            // then
            assertThat(result.shipDate()).isEqualTo(shipDate);
            assertThat(result.status()).isEqualTo(OrderStatus.PLACED);
        }

        @Test
        void shouldBeSuccessfulWhenShipDateIsRemoved() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);
            order.setShipDate(LocalDate.of(2026, 7, 1));

            JsonArray patch = createPatch(
                createPatchOperation("remove", "/shipDate")
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // when
            OrderModel result = orderService.patchOrder(orderId, patch);

            // then
            assertThat(result.shipDate()).isNull();
        }

        @Test
        void shouldThrowBusinessObjectNotFoundExceptionWhenOrderDoesNotExist() {
            // given
            UUID orderId = UUID.randomUUID();

            JsonArray patch = createPatch(createPatchOperation("replace", "/status", "approved"));

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> orderService.patchOrder(orderId, patch))
                .hasMessage("Order [" + orderId + "] not found")
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessObjectNotFoundException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.ORDER_NOT_FOUND);
        }

        @Test
        void shouldThrowBadRequestExceptionWhenPatchCannotBeApplied() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            JsonArray patch = createPatch(
                createPatchOperation("replace", "/unknownPath", "x")
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when / then
            assertThatThrownBy(() -> orderService.patchOrder(orderId, patch))
                .asInstanceOf(InstanceOfAssertFactories.type(BadRequestException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.PATCH_NOT_APPLICABLE);
        }

        @Test
        void shouldThrowBadRequestExceptionWhenPatchContentIsInvalid() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            JsonArray patch = createPatch(
                createPatchOperation("add", "/petId", "x")
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when / then
            assertThatThrownBy(() -> orderService.patchOrder(orderId, patch))
                .asInstanceOf(InstanceOfAssertFactories.type(BadRequestException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.INVALID_PATCH_CONTENT);
        }

        @Test
        void shouldThrowBadRequestExceptionWhenResultingStateIsInvalid() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            JsonArray patch = createPatch(
                createPatchOperation("remove", "/status")
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when / then
            assertThatThrownBy(() -> orderService.patchOrder(orderId, patch))
                .asInstanceOf(InstanceOfAssertFactories.type(BadRequestException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.INVALID_RESULTING_STATE);
        }

        @Test
        void shouldThrowBusinessExceptionWhenStatusTransitionIsIllegal() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.SOLD);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.APPROVED);

            JsonArray patch = createPatch(
                createPatchOperation("replace", "/status", "placed")
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when / then
            assertThatThrownBy(() -> orderService.patchOrder(orderId, patch))
                .isInstanceOf(BusinessException.class)
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.ILLEGAL_STATUS_TRANSITION);
        }

        @Test
        void shouldThrowBadRequestExceptionWhenShipDateIsInPast() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            JsonArray patch = createPatch(
                createPatchOperation("add", "/shipDate", LocalDate.now().minusDays(1).toString())
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when / then
            assertThatThrownBy(() -> orderService.patchOrder(orderId, patch))
                .asInstanceOf(InstanceOfAssertFactories.type(BadRequestException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.SHIP_DATE_IN_PAST);
        }

        @Test
        void shouldThrowBusinessExceptionWhenShipDateMissingForApprovedStatus() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.PENDING);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.PLACED);

            JsonArray patch = createPatch(
                createPatchOperation("replace", "/status", "approved")
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when / then
            assertThatThrownBy(() -> orderService.patchOrder(orderId, patch))
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.SHIP_DATE_REQUIRED);
        }

        @Test
        void shouldThrowBusinessExceptionWhenOrderIsDelivered() {
            // given
            UUID petId = UUID.randomUUID();
            Pet pet = createPet(petId, PetStatus.SOLD);

            UUID orderId = UUID.randomUUID();
            Order order = createOrder(orderId, pet, OrderStatus.DELIVERED);

            JsonArray patch = createPatch(
                createPatchOperation("add", "/shipDate", LocalDate.now().plusMonths(1).toString())
            );

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when / then
            assertThatThrownBy(() -> orderService.patchOrder(orderId, patch))
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessException.class))
                .extracting(BaseException::getProblemType)
                .isEqualTo(OrderProblemType.ORDER_NOT_MODIFIABLE);
        }
    }

    private static Pet createPet(UUID id, PetStatus status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName("Rex");
        pet.setStatus(status);
        return pet;
    }

    private static Order createOrder(UUID id, Pet pet, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setPet(pet);
        order.setStatus(status);
        return order;
    }

    private static JsonArray createPatch(JsonObject... operations) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (JsonObject operation : operations) {
            builder.add(operation);
        }
        return builder.build();
    }

    private static JsonObject createPatchOperation(String op, String path, String value) {
        return Json.createObjectBuilder()
            .add("op", op)
            .add("path", path)
            .add("value", value)
            .build();
    }

    private static JsonObject createPatchOperation(String op, String path) {
        return Json.createObjectBuilder()
            .add("op", op)
            .add("path", path)
            .build();
    }

}
