package sarf.commons8.util;

import java.util.function.Predicate;
import java.util.stream.Stream;

import static sarf.commons8.util.StreamUtil.streamFrom;

public interface PredicateUtil {

  @SuppressWarnings("unchecked")
  static <T> Predicate<T> always() {
    return Constants.always;
  }

  @SuppressWarnings("unchecked")
  static <T> Predicate<T> never() {
    return Constants.never;
  }

  @SafeVarargs
  static <T> Predicate<T> all(Predicate<T>... predicates) {
    return all(streamFrom(predicates));
  }

  static <V, T extends Predicate<V>> Predicate<V> all(T predicates) {
    return all(streamFrom(predicates));
  }

  static <T> Predicate<T> all(Stream<Predicate<T>> predicates) {
    return streamFrom(predicates)
        .reduce(Predicate::and)
        .orElse(always());
  }

  // none is just all().negate()

  @SafeVarargs
  static <T> Predicate<T> none(Predicate<T>... predicates) {
    return all(predicates).negate();
  }

  static <V, T extends Predicate<V>> Predicate<V> none(T predicates) {
    return all(predicates).negate();
  }

  static <T> Predicate<T> none(Stream<Predicate<T>> predicates) {
    return all(predicates).negate();
  }


  @SafeVarargs
  static <T> boolean isAny(Predicate<T> predicate, T... objects) {
    return streamFrom(objects).anyMatch(predicate);
  }

  @SafeVarargs
  static <T> boolean isAll(Predicate<T> predicate, T... objects) {
    return streamFrom(objects).allMatch(predicate);
  }


}
