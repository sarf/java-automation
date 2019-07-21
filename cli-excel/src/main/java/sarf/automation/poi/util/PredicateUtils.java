package sarf.automation.poi.util;

import static sarf.automation.poi.util.StreamUtils.streamFrom;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

public class PredicateUtils {

  private PredicateUtils() {

  }

  private static final Predicate ALWAYS = s -> true;
  private static final Predicate NEVER = s -> false;

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> always() {
    return ALWAYS;
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> never() {
    return NEVER;
  }

  @SafeVarargs
  public static <T> Predicate<T> any(Predicate<T>... predicates) {
    return any(streamFrom(predicates));
  }

  public static <T> Predicate<T> any(Collection<Predicate<T>> predicates) {
    return any(streamFrom(predicates));
  }

  public static <T> Predicate<T> any(Stream<Predicate<T>> predicates) {
    return streamFrom(predicates)
        .reduce(Predicate::or)
        .orElse(always());
  }

  @SafeVarargs
  public static <T> Predicate<T> all(Predicate<T>... predicates) {
    return all(streamFrom(predicates));
  }

  public static <T> Predicate<T> all(Collection<Predicate<T>> predicates) {
    return all(streamFrom(predicates));
  }

  public static <T> Predicate<T> all(Stream<Predicate<T>> predicates) {
    return streamFrom(predicates)
        .reduce(Predicate::and)
        .orElse(always());
  }

  @SafeVarargs
  public static <T> boolean isAny(Predicate<T> predicate, T... objects) {
    return streamFrom(objects).anyMatch(predicate);
  }

  @SafeVarargs
  public static <T> boolean isAll(Predicate<T> predicate, T... objects) {
    return streamFrom(objects).allMatch(predicate);
  }

  @Data
  @Getter(AccessLevel.PROTECTED)
  public static class PredicateUnaryOperator<T> {

    private final Predicate<T> predicate;
    private final UnaryOperator<T> unaryOperator;

    public T apply(T entity) {
      if (predicate.test(entity)) {
        return unaryOperator.apply(entity);
      }
      return entity;
    }
  }
}
