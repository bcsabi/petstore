package hu.bcsabi.petstore.common.jpa.entity;

import java.io.Serializable;

/**
 * Interface for all JPA entities that have a unique identifier.
 *
 * @param <ID> the identifier type
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public interface IIdentifiedEntity<ID extends Serializable> {

    /**
     * Returns the unique identifier of this entity.
     *
     * @return the unique identifier
     */
    ID getId();

    /**
     * Sets the unique identifier of this entity.
     *
     * @param id the unique identifier to set
     */
    void setId(ID id);

}
