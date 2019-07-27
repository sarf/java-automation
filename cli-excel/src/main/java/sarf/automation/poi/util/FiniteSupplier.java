package sarf.automation.poi.util;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
public class FiniteSupplier<V> implements Supplier<V> {

  @Getter(AccessLevel.PROTECTED)
  @NonNull
  private final Supplier<V> supplier;

  @NonNull
  private final Predicate<V> isFiniteValue;

  private V finiteValue;

  public FiniteSupplier(@NonNull Supplier<V> supplier) {
    this(supplier, Objects::isNull);
  }

  public FiniteSupplier(@NonNull Supplier<V> supplier, @NonNull Predicate<V> isFiniteValue) {
    this.supplier = supplier;
    this.isFiniteValue = isFiniteValue;
  }

  boolean finiteValueReached = false;

  public boolean hasReachedEnd() {
    return finiteValueReached;
  }

  @Override
  public V get() {
    if (finiteValueReached) {
      return finiteValue;
    }
    V value = get();
    if (isFiniteValue.test(value)) {
      finiteValue = value;
      finiteValueReached = true;
    }
    return value;
  }
}
