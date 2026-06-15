package hu.bcsabi.petstore.common.grpc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration for the gRPC problem responses.
 *
 * @param includeDetail controls whether the raw exception message is exposed to the client
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@ConfigurationProperties("petstore.problem")
public record GrpcProblemProperties(@DefaultValue("NEVER") IncludeDetail includeDetail) {

    /**
     * Controls whether the detail is exposed to the client.
     *
     * @author csaba.balogh
     * @since 0.1.0
     */
    public enum IncludeDetail {

        /**
         * Never expose the detail.
         */
        NEVER,

        /**
         * Always expose the detail.
         */
        ALWAYS,

    }

}
