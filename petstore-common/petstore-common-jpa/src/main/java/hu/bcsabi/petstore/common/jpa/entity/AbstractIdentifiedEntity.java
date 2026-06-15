package hu.bcsabi.petstore.common.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.UuidGenerator;

/**
 * Base {@link MappedSuperclass} for all identifiable JPA entities, providing a {@link UUID} identifier generated using {@link UuidGenerator.Style#VERSION_7}.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@MappedSuperclass
public abstract class AbstractIdentifiedEntity implements IIdentifiedEntity<UUID> {

    /**
     * The unique identifier.
     */
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(name = "x_id", updatable = false, nullable = false)
    private UUID id;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

}
