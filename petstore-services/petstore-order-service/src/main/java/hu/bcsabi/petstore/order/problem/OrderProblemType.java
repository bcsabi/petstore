package hu.bcsabi.petstore.order.problem;

import hu.bcsabi.petstore.common.core.problem.ProblemType;

/**
 * Problem types for the order service.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public enum OrderProblemType implements ProblemType {

    PET_NOT_FOUND("pet-not-found"),

    ORDER_NOT_FOUND("order-not-found"),

    PET_NOT_AVAILABLE("pet-not-available"),

    ORDER_CANNOT_BE_DELETED("order-cannot-be-deleted"),

    INVALID_DATE_RANGE("invalid-date-range"),

    INVALID_DATE_FORMAT("invalid-date-format"),

    INVALID_ORDER_ID("invalid-order-id"),

    PATCH_NOT_APPLICABLE("patch-not-applicable"),

    INVALID_PATCH_CONTENT("invalid-patch-content"),

    INVALID_RESULTING_STATE("invalid-resulting-state"),

    ILLEGAL_STATUS_TRANSITION("illegal-status-transition"),

    SHIP_DATE_REQUIRED("ship-date-required"),

    SHIP_DATE_IN_PAST("ship-date-in-past"),

    ORDER_NOT_MODIFIABLE("order-not-modifiable"),

    UNAUTHORIZED("unauthorized"),

    ;

    private final String code;

    OrderProblemType(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

}
