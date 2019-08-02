package sarf.commons12.extensions;

import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@SuppressWarnings("WeakerAccess")
public class Timeout {

    private static final Map<ChronoUnit, TimeUnit> CHRONO_TO_TIME = Stream.of(TimeUnit.values())
            .collect(Collectors.toMap(TimeUnit::toChronoUnit, Function.identity()));

    private final long amount;
    private final TimeUnit timeUnit;
    private final ChronoUnit chronoUnit;

    private Timeout(long amount, @NonNull TimeUnit timeUnit, @NonNull ChronoUnit chronoUnit) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount is not allowed to be less than zero");
        }
        if (!timeUnit.toChronoUnit().equals(chronoUnit)) {
            throw new IllegalArgumentException("timeUnit and chronoUnit must be equal");
        }
        this.amount = amount;
        this.timeUnit = timeUnit;
        this.chronoUnit = chronoUnit;
    }

    public Timeout of(long amount, TimeUnit timeUnit) {
        return new Timeout(amount, timeUnit, timeUnit.toChronoUnit());
    }

    public Timeout of(long amount, ChronoUnit chronoUnit) {
        return new Timeout(amount, CHRONO_TO_TIME.get(chronoUnit), chronoUnit);
    }

    public long getAmount() {
        return amount;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }

    public Instant from(Instant when) {
        return when.plus(getAmount(), getChronoUnit());
    }

}
