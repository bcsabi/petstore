package hu.bcsabi.petstore.order.domain;

/**
 * Availability status of a {@link Pet}.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public enum PetStatus {

    /**
     * The pet is available and can be ordered.
     */
    AVAILABLE,

    /**
     * The pet has been reserved by an order, but not yet sold.
     */
    PENDING,

    /**
     * The pet has been sold.
     */
    SOLD,

    ;

}
