package sarf.commons12.extensions;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Marker interface that indicates that the Predicate so marked is Stateful, and thus may change its test result over
 * time. This is mainly done to make it possible for certain classes to notify the developer that a different
 * behaviour from a regular Predicate is expected.
 * <p>
 * Uses: Mainly for when one wishes to mix regular Predicates with, say, a predicate that "times out" after a certain
 * amount of time.
 *
 * @param <T> the type that the StatefulPredicate is operating on. Some StatefulPredicate:s result is totally
 *            independent of the value under test (the timeout mentioned earlier), others may be depedent on the state
 *            of the object (and not just in the regular test:ing way).
 */
public interface StatefulPredicate<T> extends Predicate<T> {

    /**
     * Overrides superclass method and makes the return type more specialized
     */
    @Override
    default StatefulPredicate<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return t -> test(t) && other.test(t);
    }

    /**
     * Overrides superclass method and makes the return type more specialized
     */
    @Override
    default StatefulPredicate<T> negate() {
        return t -> !test(t);
    }

    @Override
    default StatefulPredicate<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return t -> test(t) || other.test(t);
    }

}
