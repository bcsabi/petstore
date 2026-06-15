package hu.bcsabi.petstore.common.core.exception;

import hu.bcsabi.petstore.common.core.problem.ProblemType;

/**
 * Exception for technical errors.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public class TechnicalException extends BaseException {

    public TechnicalException(ProblemType problemType, String message) {
        super(problemType, message);
    }

    public TechnicalException(ProblemType problemType, String message, Throwable cause) {
        super(problemType, message, cause);
    }
}
