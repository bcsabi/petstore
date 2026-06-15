package hu.bcsabi.petstore.order.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import hu.bcsabi.petstore.common.jpa.entity.AbstractIdentifiedAndAuditedEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * A customer order placed for a pet.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends AbstractIdentifiedAndAuditedEntity {

    /**
     * The ordered pet.
     */
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    /**
     * The requested shipping date.
     */
    @Column(name = "ship_date")
    private LocalDate shipDate;

    /**
     * The order's lifecycle status.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

}
