package hu.bcsabi.petstore.common.grpc.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 * Intercepts gRPC calls and logs the request and response lines.
 *
 * <p>Ordered just after the gRPC observation interceptor (which runs at {@code @Order(0)}) so
 * the call runs inside the tracing scope and the log lines carry the {@code traceId}, yet still
 * ahead of the (unordered, therefore innermost) API key interceptor so that rejected calls are
 * logged too.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Order(1)
public class LoggingServerInterceptor implements ServerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingServerInterceptor.class);

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String method = call.getMethodDescriptor().getFullMethodName();

        logRequestLine(method);

        ServerCall<ReqT, RespT> loggingCall = new SimpleForwardingServerCall<>(call) {

            @Override
            public void close(Status status, Metadata trailers) {
                logResponseLine(method, status);
                super.close(status, trailers);
            }
        };

        return next.startCall(loggingCall, headers);
    }

    private void logRequestLine(String method) {
        LOG.info("Incoming gRPC request: {}", method);
    }

    private void logResponseLine(String method, Status status) {
        LOG.info("Outgoing gRPC response: {}, code: {}", method, status.getCode());
    }

}
