package hu.bcsabi.petstore.order.grpc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;

import hu.bcsabi.petstore.order.security.ApiKeyProperties;

import io.grpc.ServerInterceptor;

/**
 * API key security config for the gRPC endpoints.
 * Registers a global interceptor that enforces the same {@code x-api-key} shared key as the REST API.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Configuration
public class GrpcSecurityConfig {

    @Bean
    @GlobalServerInterceptor
    public ServerInterceptor apiKeyServerInterceptor(ApiKeyProperties apiKeyProperties) {
        return new ApiKeyServerInterceptor(apiKeyProperties.apiKey());
    }

}
