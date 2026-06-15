package hu.bcsabi.petstore.common.jpa.entity;

import java.time.OffsetDateTime;

/**
 * Interface for all JPA entities that support auditing.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public interface IAuditedEntity {

    /**
     * Returns the creation timestamp of this entity.
     *
     * @return the creation timestamp
     */
    OffsetDateTime getCreatedAt();

    /**
     * Sets the creation timestamp of this entity.
     *
     * @param createdAt the creation timestamp to set
     */
    void setCreatedAt(OffsetDateTime createdAt);

    /**
     * Returns the last modification timestamp of this entity.
     *
     * @return the last modification timestamp
     */
    OffsetDateTime getLastModifiedAt();

    /**
     * Sets the last modification timestamp of this entity.
     *
     * @param lastModifiedAt the last modification timestamp to set
     */
    void setLastModifiedAt(OffsetDateTime lastModifiedAt);

    /**
     * Returns the revision counter of this entity.
     *
     * @return the revision counter
     */
    long getVersion();

    /**
     * Sets the revision counter of this entity.
     *
     * @param version the revision counter to set
     */
    void setVersion(long version);

}
