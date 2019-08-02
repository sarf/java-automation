package sarf.commons8.util.extensions;

import sarf.commons8.extensions.StatefulPredicate;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

import static sarf.commons8.util.extensions.Constants.never;

class NumberOfTimesPredicate<T> implements StatefulPredicate<T> {

    private final AtomicLong timesLeft = new AtomicLong(0);
    private BooleanSupplier testResult;

    NumberOfTimesPredicate(long times) {
        if (times < 0) {
            throw new IllegalStateException("times was less than zero");
        }
        timesLeft.set(times);
        testResult = () -> {
            if (timesLeft.decrementAndGet() == 0) {
                testResult = never;
                return false;
            }
            return true;
        };
    }

    @Override
    public boolean test(T t) {
        return testResult.getAsBoolean();
    }
}
