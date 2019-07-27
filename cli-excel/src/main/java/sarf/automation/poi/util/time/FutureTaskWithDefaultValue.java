package sarf.automation.poi.util.time;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import lombok.NonNull;

class FutureTaskWithDefaultValue<V> extends FutureTask<V> {

  private final Supplier<V> defaultValue;

  public FutureTaskWithDefaultValue(Callable<V> callable, Supplier<V> defaultValue) {
    super(callable);
    this.defaultValue = defaultValue;
  }

  public FutureTaskWithDefaultValue(Runnable runnable, V result, Supplier<V> defaultValue) {
    super(runnable, result);
    this.defaultValue = defaultValue;
  }

  public V getDefaultValue() {
    if (defaultValue != null) {
      return defaultValue.get();
    }
    return null;
  }

  public V getOrDefault() {
    return getOrDefault(this);
  }

  public static <V> V getOrDefault(@NonNull FutureTaskWithDefaultValue<V> futureTask) {
    return InterruptAfter.getQuiet(futureTask, futureTask::getDefaultValue);
  }

}
