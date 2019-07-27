package sarf.automation.poi.util;

import static sarf.automation.poi.util.CollectionUtils.addAll;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
class MultiIterator<T> implements Iterator<T> {

  @NonNull
  @Getter(AccessLevel.PROTECTED)
  private final Supplier<Optional<Iterator<T>>> iterators;

  private Iterator<T> current;

  private Supplier<Iterator<T>> acquireIterator = this::acquireCurrentIterator;

  public MultiIterator(@NonNull Collection<Iterator<T>> iterators) {
    this(CollectionUtils.toFiniteSupplier(Function.identity(), addAll(new LinkedList<>(), iterators)));
  }

  public MultiIterator(@NonNull Iterator<T>... iterators) {
    this(CollectionUtils.toFiniteSupplier(Function.identity(), addAll(new LinkedList<>(), iterators)));
  }

  public MultiIterator(@NonNull Supplier<Optional<Iterator<T>>> iterators) {
    this.iterators = iterators;
  }

  protected Iterator<T> acquireCurrentIterator() {
    Iterator<T> next = current;
    while (next == null || !next.hasNext()) {
      next = iterators.get().orElse(null);
      if (next == null) {
        acquireIterator = Collections::emptyIterator;
        return acquireIterator.get();
      }
    }
    current = next;
    return next;
  }

  @Override
  public boolean hasNext() {
    return acquireIterator.get().hasNext();
  }

  @Override
  public T next() {
    return acquireIterator.get().next();
  }
}
