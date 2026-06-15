package hu.bcsabi.petstore.order.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.bcsabi.petstore.order.domain.Pet;

/**
 * Spring Data JPA repository for {@link Pet} entities.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public interface PetRepository extends JpaRepository<Pet, UUID> {
}
