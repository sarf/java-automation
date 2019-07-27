package sarf.automation.poi.util;

import java.util.Iterator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
class MultiIterable<T> implements Iterable<T> {

  @NonNull
  @Getter(AccessLevel.PROTECTED)
  private final Iterable<T>[] iterables;

  @Override
  public Iterator<T> iterator() {
    return new MultiIterator<>(CollectionUtils.toFiniteSupplier(Iterable::iterator, iterables));
  }
}
