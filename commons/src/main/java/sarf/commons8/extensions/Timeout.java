package sarf.commons8.extensions;

import static java.util.concurrent.TimeUnit.values;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;
import lombok.NonNull;

@Data
@SuppressWarnings("WeakerAccess")
public class Timeout {

    private static final Map<ChronoUnit, TimeUnit> CHRONO_TO_TIME = createChronoUnitToTimeUnitMap();
    private static final Map<TimeUnit, ChronoUnit> TIME_TO_CHRONO = createTimeUnitToChronoUnitMap();
    private final long amount;
    private final TimeUnit timeUnit;
    private final ChronoUnit chronoUnit;

    private Timeout(long amount, @NonNull TimeUnit timeUnit, @NonNull ChronoUnit chronoUnit) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount is not allowed to be less than zero");
        }
        if (!TIME_TO_CHRONO.get(timeUnit).equals(chronoUnit)) {
            throw new IllegalArgumentException("timeUnit and chronoUnit must be equal");
        }
        this.amount = amount;
        this.timeUnit = timeUnit;
        this.chronoUnit = chronoUnit;
    }

    private static Map<TimeUnit, ChronoUnit> createTimeUnitToChronoUnitMap() {
        return Stream.of(values())
                     .collect(Collectors.toMap(Function.identity(), Timeout::toChronoUnit));
    }

    private static Map<ChronoUnit, TimeUnit> createChronoUnitToTimeUnitMap() {
        return Stream.of(values())
                     .collect(Collectors.toMap(Timeout::toChronoUnit, Function.identity()));
    }

    private static ChronoUnit toChronoUnit(@NonNull TimeUnit t) {
        try {
            return ChronoUnit.valueOf(t.name());
        } catch (IllegalArgumentException ex) {
            if (t == TimeUnit.NANOSECONDS) {
                return ChronoUnit.NANOS;
            } else if (t == TimeUnit.MICROSECONDS) {
                return ChronoUnit.MICROS;
            } else if (t == TimeUnit.MILLISECONDS) {
                return ChronoUnit.MILLIS;
            }
            throw new IllegalArgumentException(String.format("Can not match timeUnit %s to ChronoUnit", t));
        }
    }

    public Timeout of(long amount, TimeUnit timeUnit) {
        return new Timeout(amount, timeUnit, TIME_TO_CHRONO.get(timeUnit));
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
