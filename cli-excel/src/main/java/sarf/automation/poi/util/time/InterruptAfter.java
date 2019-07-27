package sarf.automation.poi.util.time;

import static java.lang.String.format;
import static java.util.function.Predicate.not;
import static sarf.automation.poi.util.FunctionUtils.doIf;
import static sarf.automation.poi.util.StringUtils.toStackTrace;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;
import java.util.logging.Logger;
import lombok.NonNull;

public class InterruptAfter {

  private static final Logger logger = Logger.getLogger(InterruptAfter.class.getName());

  private InterruptAfter() {
    throw new UnsupportedOperationException(getClass() + " is not meant to be instantiated");
  }

  private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors
      .unconfigurableScheduledExecutorService(Executors.newScheduledThreadPool(1000));
  private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);


  /**
   * Basically, this performs callable and uses a scheduled timeout to cancel it if the timeout is reached before it is
   * finished.
   *
   * @param timeoutData how long the timeout is
   * @param callable a Callable that performs the
   */
  public static <T> T interruptAfter(@NonNull TimeoutData timeoutData, @NonNull Callable<T> callable,
      Supplier<T> defaultValue) {
    FutureTaskWithDefaultValue<T> futureTask = new FutureTaskWithDefaultValue<>(callable, defaultValue);
    Runnable interrupt = () -> doIf(futureTask, not(FutureTask::isDone), InterruptAfter::cancelAllowInterrupt);
    ScheduledFuture<?> schedule = SCHEDULED_EXECUTOR_SERVICE
        .schedule(interrupt, timeoutData.getAmount(), timeoutData.getUnit());
    Runnable cancelInterrupt = () -> doIf(schedule, not(ScheduledFuture::isDone),
                                          InterruptAfter::cancelAllowInterrupt);
    EXECUTOR.submit(oneAfterAnother(futureTask, cancelInterrupt));
    return futureTask.getOrDefault();
  }
  public static <T> void interruptAfter(@NonNull TimeoutData timeoutData, @NonNull Runnable runnable) {
    interruptAfter(timeoutData, () -> { runnable.run(); return ""; }, () -> null);
  }

  static <T> void cancelAllowInterrupt(FutureTask<T> futureTask) {
    if (futureTask != null) {
      futureTask.cancel(true);
    }
  }

  static <T> void cancelAllowInterrupt(ScheduledFuture<T> futureTask) {
    if (futureTask != null) {
      futureTask.cancel(true);
    }
  }

  static <T> T getQuiet(@NonNull FutureTask<T> futureTask, Supplier<T> defaultValue) {
    T value = null;
    try {
      value = futureTask.get();
    } catch (InterruptedException ex) {
      logger.finest(
          () -> format("Interrupted while running. This is expected if it timed out. %s- %s", ex, toStackTrace(ex)));
      value = defaultValue.get();
    } catch (Exception ex) {
      logger.fine(() -> format("Problem while performing nested call. %s - %s", ex, toStackTrace(ex)));
      value = defaultValue.get();
    }
    return value;
  }

  static <T> Callable<T> oneAfterAnother(FutureTaskWithDefaultValue<T> first, Runnable second) {
    return () -> {
      T value = first.getOrDefault();
      second.run();
      return value;
    };
  }

}
