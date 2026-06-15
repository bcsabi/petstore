package hu.bcsabi.petstore.common.core.exception;

import hu.bcsabi.petstore.common.core.problem.ProblemType;

/**
 * Exception for bad / invalid requests.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public class BadRequestException extends BaseException {

    public BadRequestException(ProblemType problemType, String message) {
        super(problemType, message);
    }

    public BadRequestException(ProblemType problemType, String message, Throwable cause) {
        super(problemType, message, cause);
    }

}
