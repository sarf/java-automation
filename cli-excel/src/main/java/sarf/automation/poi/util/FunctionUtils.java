package sarf.automation.poi.util;

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

  static <T> T perform(T entity, Consumer<T> consumer) {
    if (entity != null) {
      consumer.accept(entity);
    }
    return entity;
  }

  static <T,U> T perform(T entity, Function<T,U> consumer) {
    if (entity != null) {
      consumer.apply(entity);
    }
    return entity;
  }

  static <T> T perform(T entity, UnaryOperator<T> consumer) {
    if (entity != null) {
      consumer.apply(entity);
    }
    return entity;
  }

  static <T,U, R> T perform(T entity, U other, BiFunction<T, U, R> consumer) {
    if (entity != null) {
      consumer.apply(entity, other);
    }
    return entity;
  }

  static <T> T execute(T entity, Consumer<T> consumer) {
    if (entity != null) {
      consumer.accept(entity);
    }
    return entity;
  }

}
