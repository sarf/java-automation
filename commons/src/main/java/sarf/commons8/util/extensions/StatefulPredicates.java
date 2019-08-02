package sarf.commons8.util.extensions;

import lombok.NonNull;
import sarf.commons8.extensions.StatefulPredicate;
import sarf.commons8.extensions.Timeout;

import java.time.Instant;

public interface StatefulPredicates {

    static <T> StatefulPredicate<T> timeOutAfter(@NonNull Timeout timeout) {
        return new TimeoutPredicate<>(timeout.from(Instant.now()));
    }

    static <T> StatefulPredicate<T> timeOutAt(@NonNull Instant instant) {
        return new TimeoutPredicate<>(instant);
    }

    static <T> StatefulPredicate<T> afterTimes(long times) {
        return new NumberOfTimesPredicate<>(times);
    }

}
