package sarf.automation.poi.util;

import static sarf.automation.poi.util.StreamUtils.streamFrom;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CollectionUtils {

  private CollectionUtils() {
    throw new UnsupportedOperationException(getClass() + " is not meant to be instantiated");
  }

  public static <T> boolean isEmpty(Collection<T> collection) {
    return collection == null || collection.isEmpty();
  }

  public static <T> boolean isNotEmpty(Collection<T> collection) {
    return !isEmpty(collection);
  }

  public static <T> Predicate<Integer> indexValid(List<T> list) {
    return indexValid(0, list.size());
  }

  public static <T extends Number> Predicate<T> indexValid(T min, T max) {
    return i -> i.longValue() >= min.longValue() && i.longValue() <= max.longValue();
  }

  public static <T extends Collection<U>, U> Optional<T> toOpt(T arg) {
    return isEmpty(arg) ? Optional.empty() : Optional.of(arg);
  }


  public static <T> Iterable<T> chainedIterable(Iterable<T>... iterables) {
    return new MultiIterable<>(iterables);
  }

  public static <T extends Collection<U>, R extends Collection<U>, U> T addAll(T dest, R source) {
    if (dest != null && isNotEmpty(source)) {
      dest.addAll(source);
    }
    return dest;
  }

  public static <T extends Collection<U>, R extends Stream<U>, U> T addAll(T dest, R source) {
    if (dest != null && source != null) {
      source.forEach(dest::add);
    }
    return dest;
  }

  @SafeVarargs
  public static <T extends Collection<U>, U> T addAll(T dest, U... source) {
    if (dest != null && ArrayUtils.isNotEmpty(source)) {
      dest.addAll(Arrays.asList(source));
    }
    return dest;
  }

  @SafeVarargs
  public static <T, U> Supplier<Optional<T>> toFiniteSupplier(Function<U, T> convert, U... source) {
    return toFiniteSupplier(convert, addAll(new LinkedList<U>(), source));
  }

  public static <T, U> Supplier<Optional<T>> toFiniteSupplier(Function<U, T> convert, Collection<U> source) {
    return toFiniteSupplier(convert, addAll(new LinkedList<U>(), source));
  }

  // Internal
  private static <T, U> Supplier<Optional<T>> toFiniteSupplier(Function<U, T> convert, Deque<U> source) {
    return () -> source.isEmpty() ? Optional.empty() : Optional.of(convert.apply(source.removeFirst()));
  }

  private static <T, U extends Collection<T>> T getNonNullOrNull(U collection, Function<U, T> get) {
    T value = null;
    while(!isEmpty(collection)) {
      value = get.apply(collection);
      if(value != null) {
        break;
      }
    }
    return value;
  }

  private static <T, U> T getNextNonNull(Function<U, T> convert, Supplier<U> nextOrNull) {
    T value = null;
    while(value == null) {
      U otherValue = nextOrNull.get();
      if(otherValue == null) break;
      value = convert.apply(otherValue);
    }
    return value;
  }


  public static <T> Optional<T> findInFirstCome(Collection<T> coll, Stream<Predicate<T>> predicates) {
    return predicates
        .map(p -> coll.stream().filter(p).findAny().orElse(null))
        .findAny();
  }

  @SafeVarargs
  public static <T> Optional<T> findInFirstCome(Collection<T> coll, Predicate<T>... predicates) {
    return findInFirstCome(coll, Stream.of(predicates));
  }
}
