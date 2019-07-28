package sarf.commons.extensions;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Data
public class FiniteSupplier<V> implements Supplier<V> {

  @Getter(AccessLevel.PROTECTED)
  @NonNull
  private final Supplier<V> supplier;

  @NonNull
  private final Predicate<V> isFiniteValue;

  @Setter(AccessLevel.NONE)
  private final WriteOnce<V> finiteValue = new WriteOnce<>();

  public FiniteSupplier(@NonNull Supplier<V> supplier) {
    this(supplier, Objects::isNull);
  }

  public FiniteSupplier(@NonNull Supplier<V> supplier, @NonNull Predicate<V> isFiniteValue) {
    this.supplier = supplier;
    this.isFiniteValue = isFiniteValue;
  }

  public boolean hasReachedEnd() {
    return finiteValue.written();
  }

  @Override
  public V get() {
    if (finiteValue.written()) {
      return finiteValue.get();
    }
    V value = supplier.get();
    if (isFiniteValue.test(value)) {
      finiteValue.writeIfUnwritten(value);
    }
    return value;
  }
}