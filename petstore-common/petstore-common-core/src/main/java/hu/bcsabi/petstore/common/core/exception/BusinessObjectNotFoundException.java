package hu.bcsabi.petstore.common.core.exception;

import hu.bcsabi.petstore.common.core.problem.ProblemType;

/**
 * Exception for business object not found errors.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public class BusinessObjectNotFoundException extends BaseException {

    public BusinessObjectNotFoundException(ProblemType problemType, String message) {
        super(problemType, message);
    }
}
