package hu.bcsabi.petstore.common.jpa.specification;

import jakarta.persistence.metamodel.SingularAttribute;

import org.springframework.data.jpa.domain.Specification;

/**
 * Common query specifications.
 *
 * @author csaba.balogh
 * @since 0.1.0
 */
public final class CommonSpecifications {

    private CommonSpecifications() {
    }

    /**
     * Checks that an attribute is greater than or equal to a value.
     *
     * @param attribute the attribute to compare
     * @param value     the value to compare against (optional)
     *
     * @return a specification; unrestricted if {@code value} is {@code null}
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThanOrEqualTo(SingularAttribute<T, Y> attribute, Y value) {
        if (value == null) {
            return Specification.unrestricted();
        }
        return (root, _, cb) -> cb.greaterThanOrEqualTo(root.get(attribute), value);
    }

    /**
     * Checks that an attribute is less than or equal to a value.
     *
     * @param attribute the attribute to compare
     * @param value     the value to compare against (optional)
     *
     * @return a specification; unrestricted if {@code value} is {@code null}
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThanOrEqualTo(SingularAttribute<T, Y> attribute, Y value) {
        if (value == null) {
            return Specification.unrestricted();
        }
        return (root, _, cb) -> cb.lessThanOrEqualTo(root.get(attribute), value);
    }

}
