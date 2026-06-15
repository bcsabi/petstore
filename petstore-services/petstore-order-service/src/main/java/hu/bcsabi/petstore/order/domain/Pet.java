package hu.bcsabi.petstore.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import hu.bcsabi.petstore.common.jpa.entity.AbstractIdentifiedAndAuditedEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * A pet.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Getter
@Setter
@Entity
@Table(name = "pet")
public class Pet extends AbstractIdentifiedAndAuditedEntity {

    /**
     * The pet's name.
     */
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The pet's availability status.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PetStatus status;
}
