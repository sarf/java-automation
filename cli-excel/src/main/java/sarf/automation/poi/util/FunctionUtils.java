package sarf.automation.poi.util;

import static sarf.automation.poi.util.PredicateUtils.always;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface FunctionUtils {

  static <T, U> U map(T entity, Function<T, U> convert) {
    return optMap(entity, convert)
        .orElse(null);
  }

  static <T, U> Optional<U> optMap(T entity, Function<T, U> convert) {
    return Optional.ofNullable(entity)
                   .map(convert);
  }

  static <T> Optional<T> optFilter(T entity, Predicate<T> predicate) {
    return Optional.ofNullable(entity)
                   .filter(predicate);
  }

  static <T> Optional<T> optFilter(Optional<T> entity, Predicate<T> predicate) {
    return entity.filter(predicate);
  }

  static <T> T defaultValue(T entity, T defaultValue) {
    return defaultValue(entity, Objects::isNull, defaultValue);
  }

  static <T> T defaultValue(T entity, Supplier<T> defaultValueSupplier) {
    return defaultValue(entity, (Function<T, Boolean>) Objects::isNull, defaultValueSupplier);
  }

  static <T> T defaultValue(T entity, Function<T, Boolean> useDefaultValue, T defaultValue) {
    return defaultValue(entity, useDefaultValue, (Supplier<T>) () -> defaultValue);
  }

  static <T> T defaultValue(T entity, Function<T, Boolean> useDefaultValue, Supplier<T> defaultValueSupplier) {
    return useDefaultValue.apply(entity) ? defaultValueSupplier.get() : entity;
  }

  static <T,U> T feedFuncIgnore(T entity, Function<T,U> consumer) {
    if (entity != null) {
      consumer.apply(entity);
    }
    return entity;
  }

  static <T> T perform(T entity, UnaryOperator<T> operator) {
    return perform(entity, Objects::nonNull, operator);
  }

  static <T> T perform(T entity, Predicate<T> predicate, UnaryOperator<T> operator) {
    if (predicate.test(entity)) {
      return operator.apply(entity);
    }
    return entity;
  }

  static <T,V> Supplier<V> supplyToFunc(Supplier<T> supplier, Function<T,V> func) {
    return () -> func.apply(supplier.get());
  }

  static <T,U, R> T perform(T entity, U other, BiFunction<T, U, R> consumer) {
    if (entity != null) {
      consumer.apply(entity, other);
    }
    return entity;
  }

  static <T> T consumeRecycle(T consumedYetWhole, Consumer<T> consumer) {
    return consumeRecycle(consumedYetWhole, Objects::nonNull, consumer);
  }
  static <T> T consumeRecycle(T consumedYetWhole, Predicate<T> predicate, Consumer<T> consumer) {
    if (predicate.test(consumedYetWhole)) {
      consumer.accept(consumedYetWhole);
    }
    return consumedYetWhole;
  }

  static <T> T doIf(T entity, Predicate<T> predicate, Consumer<T> consumer) {
    if(predicate.test(entity)) {
      consumer.accept(entity);
    }
    return entity;
  }

  static <T> Supplier<T> doIfS(Supplier<T> entity, Predicate<T> predicate, Consumer<T> consumer) {
    return () -> doIf(entity.get(), predicate, consumer);
  }

  static <T> T doIf(T entity, Consumer<T> consumer) {
    return doIf(entity, always(), consumer);
  }

  static <T> Supplier<T> doIfS(Supplier<T> entity, Consumer<T> consumer) {
    return () -> doIf(entity.get(), always(), consumer);
  }

  static <T> T doIfNN(T entity, Consumer<T> consumer) {
    return doIf(entity, Objects::nonNull, consumer);
  }

  static <T> Supplier<T> doIfNNS(Supplier<T> entity, Consumer<T> consumer) {
    return () -> doIf(entity.get(), Objects::nonNull, consumer);
  }
}
