package sarf.automation.events.imap.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import lombok.NonNull;

public class ExecutionHelper {

  public static void waitUntil(@NonNull Supplier<Boolean> condition, long amount, @NonNull ChronoUnit chronoUnit) {
    Instant now = Instant.now();
    Instant expires = now.plus(amount, chronoUnit);
    Runnable sleeper = getAppropriateSleepTimeRunnable(amount, chronoUnit);

    while (!condition.get() && Instant.now().isBefore(expires)) {
      sleeper.run();
    }

    if (!Instant.now().isBefore(expires)) {
      throw new IllegalStateException(String.format("expired after %d %s", amount, chronoUnit));
    }
  }

  private static Runnable getAppropriateSleepTimeRunnable(long amount, ChronoUnit chronoUnit) {
    switch (chronoUnit) {
      case MICROS:
        return sleeper(0L, 500);
      case NANOS:
        return sleeper(0L, 1);
      case MILLIS:
        return sleeper(amount > 10 ? 5L : 1L, 0);
      default:
        return sleeper(50L, 0);
    }
  }

  static Runnable sleeper(long millis, int nanos) {
    return () -> {
      try {
        Thread.sleep(millis, nanos);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    };
  }


}
