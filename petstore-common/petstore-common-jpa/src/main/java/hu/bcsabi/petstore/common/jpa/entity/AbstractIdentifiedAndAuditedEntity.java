package hu.bcsabi.petstore.common.jpa.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Base {@link MappedSuperclass} for auditable JPA entities.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractIdentifiedAndAuditedEntity extends AbstractIdentifiedEntity implements IAuditedEntity {

    /**
     * Creation timestamp, populated automatically by JPA auditing.
     */
    @NotNull
    @CreatedDate
    @Column(name = "x_created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Last modification timestamp, populated automatically by JPA auditing.
     */
    @NotNull
    @LastModifiedDate
    @Column(name = "x_last_modified_at", nullable = false)
    private OffsetDateTime lastModifiedAt;

    /**
     * Revision counter incremented by the persistence provider on every modification.
     */
    @Version
    @Column(name = "x_version", nullable = false)
    private long version;

    @Override
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public OffsetDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    @Override
    public void setLastModifiedAt(OffsetDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }

}
