package hu.bcsabi.petstore.common.core.exception;

import hu.bcsabi.petstore.common.core.problem.ProblemType;

/**
 * Base exception for all application exceptions.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public abstract class BaseException extends RuntimeException {

    private final ProblemType problemType;

    protected BaseException(ProblemType problemType, String message) {
        super(message);
        this.problemType = problemType;
    }

    protected BaseException(ProblemType problemType, String message, Throwable cause) {
        super(message, cause);
        this.problemType = problemType;
    }

    public ProblemType getProblemType() {
        return problemType;
    }
}
