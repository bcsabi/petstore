package hu.bcsabi.petstore.order.security;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for the API key authentication.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Validated
@ConfigurationProperties("petstore.security")
public record ApiKeyProperties(@NotBlank String apiKey) {
}
