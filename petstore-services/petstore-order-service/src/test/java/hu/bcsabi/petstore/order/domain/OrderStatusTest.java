package hu.bcsabi.petstore.order.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Unit tests for {@link OrderStatus}.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
class OrderStatusTest {

    @Nested
    class CanTransitionToTest {

        @ParameterizedTest
        @MethodSource("allowedTransitions")
        void shouldAllowTransition(OrderStatus from, OrderStatus to) {
            // when
            boolean result = from.canTransitionTo(to);

            // then
            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @MethodSource("notAllowedTransitions")
        void shouldRejectTransition(OrderStatus from, OrderStatus to) {
            // when
            boolean result = from.canTransitionTo(to);

            // then
            assertThat(result).isFalse();
        }

        static Stream<Arguments> allowedTransitions() {
            // Ha a státusz nem változik, az engedve van
            Stream<Arguments> notChangedArguments = Arrays.stream(OrderStatus.values())
                .map(status -> arguments(status, status));

            // Egyel való előre lépés engedett
            Stream<Arguments> forwardChangedArguments = Stream.of(
                arguments(OrderStatus.PLACED, OrderStatus.APPROVED),
                arguments(OrderStatus.APPROVED, OrderStatus.DELIVERED)
            );

            return Stream.concat(notChangedArguments, forwardChangedArguments);
        }

        static Stream<Arguments> notAllowedTransitions() {
            // Egyik státusz sem változhat null-ra sosem
            Stream<Arguments> nullChangedArguments = Arrays.stream(OrderStatus.values())
                .map(status -> arguments(status, null));

            // Visszafelé / túlságosan előre ugrás nem engedélyezett
            Stream<Arguments> invalidChangeArguments = Stream.of(
                arguments(OrderStatus.PLACED, OrderStatus.DELIVERED),
                arguments(OrderStatus.APPROVED, OrderStatus.PLACED),
                arguments(OrderStatus.DELIVERED, OrderStatus.APPROVED),
                arguments(OrderStatus.DELIVERED, OrderStatus.PLACED)
            );

            return Stream.concat(nullChangedArguments, invalidChangeArguments);
        }
    }

}
