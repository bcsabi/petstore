package hu.bcsabi.petstore.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import hu.bcsabi.petstore.common.jpa.audit.OffsetDateTimeProvider;

/**
 * JPA configuration for the Order Service.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class JpaConfig {

    /**
     * Time source for JPA auditing.
     *
     * @return the date-time provider
     */
    @Bean
    public DateTimeProvider dateTimeProvider() {
        return new OffsetDateTimeProvider();
    }

}
