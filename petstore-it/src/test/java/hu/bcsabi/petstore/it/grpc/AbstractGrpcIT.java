package hu.bcsabi.petstore.it.grpc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;

import hu.bcsabi.petstore.it.AbstractIT;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;

/**
 * Base for gRPC integration tests: opens a plaintext channel to the running gRPC server
 * and tears it down after each test.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public abstract class AbstractGrpcIT extends AbstractIT {

    @Value("${project.grpc-target}")
    private String grpcTarget;

    protected ManagedChannel channel;

    @BeforeEach
    void setUpGrpcChannel() {
        this.channel = ManagedChannelBuilder.forTarget(grpcTarget).usePlaintext().build();
    }

    @AfterEach
    void tearDownGrpcChannel() {
        channel.shutdownNow();
    }

    protected Metadata apiKeyMetadata() {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of(API_KEY_HEADER, Metadata.ASCII_STRING_MARSHALLER), apiKey);
        return metadata;
    }

}
