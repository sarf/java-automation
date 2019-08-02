package sarf.commons12.util.extensions;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import sarf.commons12.extensions.StatefulPredicate;
import sarf.commons12.extensions.Timeout;

import java.time.Instant;

public interface StatefulPredicates {

    static <T> StatefulPredicate<T> timeOutAfter(@NotNull @NonNull Timeout timeout) {
        return new TimeoutPredicate<>(timeout.from(Instant.now()));
    }

    static <T> StatefulPredicate<T> timeOutAt(@NotNull @NonNull Instant instant) {
        return new TimeoutPredicate<>(instant);
    }

    static <T> StatefulPredicate<T> afterTimes(long times) {
        return new NumberOfTimesPredicate<>(times);
    }

}
