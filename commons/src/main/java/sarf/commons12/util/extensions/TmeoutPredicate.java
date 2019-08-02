package sarf.commons12.util.extensions;

import lombok.Data;
import sarf.commons12.extensions.StatefulPredicate;

import java.time.Instant;
import java.util.function.BooleanSupplier;

import static sarf.commons12.util.extensions.Constants.never;

@Data
class TimeoutPredicate<T> implements StatefulPredicate<T> {

    private final Instant after;

    private BooleanSupplier testSupplier;

    TimeoutPredicate(Instant after) {
        this.after = after;
        testSupplier = () -> {
            if (Instant.now().isAfter(this.after)) {
                testSupplier = never;
                return false;
            }
            return true;
        };
    }

    @Override
    public boolean test(T t) {
        return testSupplier.getAsBoolean();
    }
}
