package hu.bcsabi.petstore.common.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

import hu.bcsabi.petstore.common.web.config.ProblemProperties;
import hu.bcsabi.petstore.common.web.exception.CommonRestExceptionHandler;

/**
 * Auto configuration for the common web module.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(ProblemProperties.class)
public class CommonWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CommonRestExceptionHandler commonRestExceptionHandler(MessageSource messageSource, ProblemProperties problemProperties) {
        return new CommonRestExceptionHandler(messageSource, problemProperties);
    }

}
