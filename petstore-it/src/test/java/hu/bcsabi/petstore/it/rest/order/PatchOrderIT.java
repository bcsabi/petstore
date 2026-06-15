package hu.bcsabi.petstore.it.rest.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import hu.bcsabi.petstore.order.dto.JsonPatchOperation;
import hu.bcsabi.petstore.order.dto.OrderCreateRequest;
import hu.bcsabi.petstore.order.dto.OrderResponse;
import hu.bcsabi.petstore.order.dto.OrderStatusDto;

class PatchOrderIT extends AbstractOrderIT {

    @Test
    void shouldAddShipDate() {
        // given
        OrderCreateRequest request = new OrderCreateRequest(existingPetId);
        OrderResponse createOrderResponse = createOrder(request);
        assertThat(createOrderResponse).isNotNull();

        createdOrderId = createOrderResponse.getId();

        LocalDate shipDate = LocalDate.now();

        JsonPatchOperation addShipDateOperation = new JsonPatchOperation();
        addShipDateOperation.op(JsonPatchOperation.OpEnum.ADD);
        addShipDateOperation.path("/shipDate");
        addShipDateOperation.value(shipDate.toString());

        // when
        OrderResponse orderResponse = patchOrder(createdOrderId, List.of(addShipDateOperation));

        // then
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getPetId()).isEqualTo(existingPetId);
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse.getStatus()).isEqualTo(OrderStatusDto.PLACED);
        assertThat(orderResponse.getShipDate()).isEqualTo(shipDate);
    }

    @Test
    @Disabled("Tiltva, mert jelenleg egyszer lehet ellőni így a tesztadatot")
    void shouldAddShipDateAndSetStatusToApprove() {
        // given
        OrderCreateRequest request = new OrderCreateRequest(existingPetId);
        OrderResponse createOrderResponse = createOrder(request);
        assertThat(createOrderResponse).isNotNull();

        UUID createdOrderId = createOrderResponse.getId(); // lokális, mert nem tudjuk törölni státusz váltás után

        LocalDate shipDate = LocalDate.now();

        JsonPatchOperation addShipDateOperation = new JsonPatchOperation();
        addShipDateOperation.op(JsonPatchOperation.OpEnum.ADD);
        addShipDateOperation.path("/shipDate");
        addShipDateOperation.value(shipDate.toString());

        JsonPatchOperation replaceStatusOperation = new JsonPatchOperation();
        replaceStatusOperation.op(JsonPatchOperation.OpEnum.REPLACE);
        replaceStatusOperation.path("/status");
        replaceStatusOperation.value(OrderStatusDto.APPROVED.toString());

        List<JsonPatchOperation> operations = List.of(addShipDateOperation, replaceStatusOperation);

        // when
        OrderResponse orderResponse = patchOrder(createdOrderId, operations);

        // then
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getPetId()).isEqualTo(existingPetId);
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse.getStatus()).isEqualTo(OrderStatusDto.APPROVED);
        assertThat(orderResponse.getShipDate()).isEqualTo(shipDate);
    }

}
