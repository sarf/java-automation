package sarf.commons12.util;

import lombok.NonNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface FunctionUtil {

  static void nop() {
  }

  @SuppressWarnings("unchecked")
  static <T> Consumer<T> nopConsumer() {
    return Constants.nopConsumer;
  }

  @SuppressWarnings("unchecked")
  static <T> Supplier<T> nullSupplier() {
    return Constants.nullSupplier;
  }

  static <T> void onlyIfElse(T value, @NonNull Predicate<T> predicate, Consumer<T> whenTrue, Consumer<T> whenFalse) {
    if (predicate.test(value)) {
      if (whenTrue != null) {
        whenTrue.accept(value);
      }
    } else {
      if (whenFalse != null) {
        whenFalse.accept(value);
      }
    }
  }

  interface FunctionAliases {
    static <T extends Boolean> void whenTrue(T value, Consumer<T> whenTrue) {
      FunctionUtil.onlyIfElse(value, Boolean.TRUE::equals, whenTrue, null);
    }

    static <T extends Boolean> void whenTrue(T value, Consumer<T> whenTrue, Consumer<T> whenFalse) {
      FunctionUtil.onlyIfElse(value, Boolean.TRUE::equals, whenTrue, whenFalse);
    }

    static <T extends Boolean> void whenFalse(T value, Consumer<T> whenTrue) {
      FunctionUtil.onlyIfElse(value, Boolean.FALSE::equals, whenTrue, null);
    }

    static <T extends Boolean> void whenFalse(T value, Consumer<T> whenTrue, Consumer<T> whenFalse) {
      FunctionUtil.onlyIfElse(value, Boolean.FALSE::equals, whenTrue, whenFalse);
    }

    static <T> void whenNotNull(T value, Consumer<T> whenTrue) {
      FunctionUtil.onlyIfElse(value, Objects::nonNull, whenTrue, null);
    }

    static <T> void whenNotNull(T value, Consumer<T> whenTrue, Consumer<T> whenFalse) {
      FunctionUtil.onlyIfElse(value, Objects::nonNull, whenTrue, whenFalse);
    }

    static <T> void whenNull(T value, Consumer<T> whenTrue) {
      FunctionUtil.onlyIfElse(value, Objects::isNull, whenTrue, null);
    }

    static <T> void whenNull(T value, Consumer<T> whenTrue, Consumer<T> whenFalse) {
      FunctionUtil.onlyIfElse(value, Objects::isNull, whenTrue, whenFalse);
    }

    static <T> void when(T value, @NonNull Predicate<T> predicate, Consumer<T> whenTrue) {
      FunctionUtil.onlyIfElse(value, predicate, whenTrue, null);
    }

    static <T> void when(T value, @NonNull Predicate<T> predicate, Consumer<T> whenTrue, Consumer<T> whenFalse) {
      FunctionUtil.onlyIfElse(value, predicate, whenTrue, whenFalse);
    }
  }


}
