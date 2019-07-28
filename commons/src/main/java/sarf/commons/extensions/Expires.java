package sarf.commons.extensions;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.NonNull;

public class Expires<T> {

  private T value;
  private Supplier<Boolean> isExpired;

  public static Supplier<Boolean> expiresIn(long amount, TemporalUnit temporalUnit) {
    Instant expiresAt = Instant.now().plus(amount, temporalUnit);
    return expiresAt(expiresAt);
  }

  public static Supplier<Boolean> expiresAt(@NonNull Instant expiresAt) {
    return () -> Instant.now().isAfter(expiresAt);
  }

  public static Supplier<Boolean> expiresAfterBoth(@NonNull Supplier<Boolean> first,
      @NonNull Supplier<Boolean> second) {
    return () -> first.get() && second.get();
  }

  public static Supplier<Boolean> expiresAfterEither(@NonNull Supplier<Boolean> first,
      @NonNull Supplier<Boolean> second) {
    return () -> first.get() || second.get();
  }

  public Supplier<Boolean> expiresDepending(@NonNull Predicate<T> predicate) {
    return () -> predicate.test(value);
  }

  public void set(T value, @NonNull Supplier<Boolean> isExpired) {
    this.value = value;
    this.isExpired = isExpired;
  }

  public T get() {
    if (isExpired.get()) {
      value = null;
      return null;
    }
    return value;
  }

}
