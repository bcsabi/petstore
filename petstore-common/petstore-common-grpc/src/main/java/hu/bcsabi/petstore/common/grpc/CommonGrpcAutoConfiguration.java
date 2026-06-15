package hu.bcsabi.petstore.common.grpc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.server.GlobalServerInterceptor;

import hu.bcsabi.petstore.common.grpc.config.GrpcProblemProperties;
import hu.bcsabi.petstore.common.grpc.exception.CommonGrpcExceptionAdvice;
import hu.bcsabi.petstore.common.grpc.logging.LoggingServerInterceptor;

import io.grpc.ServerInterceptor;

/**
 * Auto configuration for the common gRPC module.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@AutoConfiguration
@ConditionalOnClass(ServerInterceptor.class)
@EnableConfigurationProperties(GrpcProblemProperties.class)
public class CommonGrpcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CommonGrpcExceptionAdvice grpcExceptionAdvice(MessageSource messageSource, GrpcProblemProperties grpcProblemProperties) {
        return new CommonGrpcExceptionAdvice(messageSource, grpcProblemProperties);
    }

    @Bean
    @GlobalServerInterceptor
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "petstore.logging.grpc", name = "enabled", havingValue = "true", matchIfMissing = true)
    public LoggingServerInterceptor loggingServerInterceptor() {
        return new LoggingServerInterceptor();
    }

}
