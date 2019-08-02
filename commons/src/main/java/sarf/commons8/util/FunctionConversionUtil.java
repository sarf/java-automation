package sarf.commons8.util;

import lombok.NonNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface FunctionConversionUtil {


  @NonNull
  static <T> Consumer<T> consumer(Runnable runnable) {
    return c -> runnable.run();
  }


  @NonNull
  static <T> Supplier<T> fork(Supplier<T> supplier, Consumer<T> consumer) {
    return () -> reuse(supplier.get(), consumer);
  }

  static <T> T reuse(T entity, Consumer<T> consumer) {
    consumer.accept(entity);
    return entity;
  }


  static <T, V> Function<T, V> tasteTest(@NonNull Function<T, V> func, @NonNull Consumer<T> voyeur) {
    return t -> func.apply(reuse(t, voyeur));
  }


  static <T, V> Function<T, V> seconds(@NonNull Function<T, V> func, @NonNull Consumer<V> voyeur) {
    return t -> reuse(func.apply(t), voyeur);
  }


  static <T> UnaryOperator<T> function(@NonNull Consumer<T> consumer) {
    return t -> reuse(t, consumer);
  }


  static <K, T> Function<K, T> function(@NonNull Supplier<T> supplier) {
    return t -> supplier.get();
  }


  static <T> UnaryOperator<T> unaryoperator(@NonNull Supplier<T> supplier) {
    return t -> supplier.get();
  }

}
