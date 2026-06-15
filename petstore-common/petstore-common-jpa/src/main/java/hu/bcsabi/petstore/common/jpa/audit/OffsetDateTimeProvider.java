package hu.bcsabi.petstore.common.jpa.audit;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import org.springframework.data.auditing.DateTimeProvider;

/**
 * {@link DateTimeProvider} implementation that returns the current {@link OffsetDateTime} in UTC.
 * This class is typically used to supply the current date-time for auditing purposes in JPA configurations.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public class OffsetDateTimeProvider implements DateTimeProvider {

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(OffsetDateTime.now(ZoneOffset.UTC));
    }

}
