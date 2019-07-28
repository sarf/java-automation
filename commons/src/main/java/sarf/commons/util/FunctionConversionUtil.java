package sarf.commons.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface FunctionConversionUtil {

  @NotNull
  @Contract(pure = true)
  @NonNull
  static <T> Consumer<T> consumer(Runnable runnable) {
    return c -> runnable.run();
  }

  @NotNull
  @Contract(pure = true)
  @NonNull
  static <T> Supplier<T> fork(Supplier<T> supplier, Consumer<T> consumer) {
    return () -> reuse(supplier.get(), consumer);
  }

  @Contract("_, _ -> param1")
  static <T> T reuse(T entity, Consumer<T> consumer) {
    consumer.accept(entity);
    return entity;
  }

  @NotNull
  @Contract(pure = true)
  static <T, V> Function<T, V> peek(@NonNull Function<T, V> func, @NonNull Consumer<T> voyeur) {
    return t -> func.apply(reuse(t, voyeur));
  }

  @NotNull
  @Contract(pure = true)
  static <T, V> Function<T, V> glance(@NonNull Function<T, V> func, @NonNull Consumer<V> voyeur) {
    return t -> reuse(func.apply(t), voyeur);
  }

}
