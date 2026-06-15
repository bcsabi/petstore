package hu.bcsabi.petstore.common.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the problem responses.
 *
 * @param baseUri       the base URI of the problem responses
 * @param includeDetail controls whether the detail field should be included in the problem response
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@ConfigurationProperties("petstore.problem")
public record ProblemProperties(String baseUri, IncludeDetail includeDetail) {

    /**
     * Controls whether the detail field should be included in the problem response.
     *
     * @author csaba.balogh
     * @since 0.1.0
     */
    public enum IncludeDetail {

        /**
         * Never include the detail field.
         */
        NEVER,

        /**
         * Always include the detail field.
         */
        ALWAYS,

    }

}
