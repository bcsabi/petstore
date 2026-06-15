package hu.bcsabi.petstore.common.core.exception;

import hu.bcsabi.petstore.common.core.problem.ProblemType;

/**
 * Exception for business errors.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public class BusinessException extends BaseException {

    public BusinessException(ProblemType problemType, String message) {
        super(problemType, message);
    }

    public BusinessException(ProblemType problemType, String message, Throwable cause) {
        super(problemType, message, cause);
    }

}
