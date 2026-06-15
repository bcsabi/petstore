package hu.bcsabi.petstore.common.core.problem;

import java.io.Serializable;

/**
 * Transport-neutral identifier for a kind of problem the application can report.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public interface ProblemType extends Serializable {

    /**
     * Returns the code that identifies the problem type.
     *
     * @return the problem code
     */
    String code();

}
