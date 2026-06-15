package hu.bcsabi.petstore.order.domain;

/**
 * Lifecycle status of an {@link Order}.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public enum OrderStatus {

    /**
     * The order has been placed.
     */
    PLACED,

    /**
     * The order has been approved.
     */
    APPROVED,

    /**
     * The order has been delivered.
     */
    DELIVERED,

    ;

    /**
     * Returns whether the transition from this status to the target status is allowed.
     *
     * @param target the target status
     *
     * @return true if the transition is allowed
     */
    public boolean canTransitionTo(OrderStatus target) {
        if (this == target) {
            return true;
        }
        return switch (this) {
            case PLACED -> target == APPROVED;
            case APPROVED -> target == DELIVERED;
            case DELIVERED -> false;
        };
    }

}
