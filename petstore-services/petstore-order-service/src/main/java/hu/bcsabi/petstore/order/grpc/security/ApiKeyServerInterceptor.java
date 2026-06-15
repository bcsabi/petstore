package hu.bcsabi.petstore.order.grpc.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 * Authenticates gRPC calls by a shared {@code x-api-key} metadata entry.
 * Mirrors the REST {@code ApiKeyAuthFilter}.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public class ApiKeyServerInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> API_KEY_HEADER = Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER);

    private final String apiKey;

    public ApiKeyServerInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String providedKey = headers.get(API_KEY_HEADER);

        if (providedKey == null || !isApiKeyValid(providedKey)) {
            call.close(Status.UNAUTHENTICATED.withDescription("Missing or invalid API key"), new Metadata());
            return new ServerCall.Listener<>() {
            };
        }

        return next.startCall(call, headers);
    }

    private boolean isApiKeyValid(String providedKey) {
        return MessageDigest.isEqual(
            providedKey.getBytes(StandardCharsets.UTF_8),
            apiKey.getBytes(StandardCharsets.UTF_8)
        );
    }

}
