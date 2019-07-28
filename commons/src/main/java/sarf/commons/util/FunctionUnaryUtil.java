package sarf.commons.util;

import static sarf.commons.util.EmptyUtil.isEmpty;
import static sarf.commons.util.StreamUtil.streamFrom;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface FunctionUnaryUtil {

  @SafeVarargs
  static <T> UnaryOperator<T> compose(UnaryOperator<T>... operators) {
    if (isEmpty(operators)) {
      return UnaryOperator.identity();
    }
    return unary(streamFrom(operators));
  }

  @NotNull
  @Contract(pure = true)
  static <T> UnaryOperator<T> unary(Function<T, T> function) {
    return function::apply;
  }

  @Contract(value = "_ -> param1", pure = true)
  static <T> Function<T, T> function(UnaryOperator<T> unary) {
    return unary;
  }

  @NonNull
  static <T> UnaryOperator<T> unary(Stream<UnaryOperator<T>> operators) {
    if (operators == null) {
      return UnaryOperator.identity();
    }
    return t -> operators.reduce(FunctionUnaryUtil::andThen).map(s -> s.apply(t)).orElse(t);
  }

  @NonNull
  static <T> Function<T, T> function(Stream<Function<T, T>> operators) {
    if (operators == null) {
      return UnaryOperator.identity();
    }
    return t -> operators.reduce(FunctionUnaryUtil::compose).map(s -> s.apply(t)).orElse(t);
  }

  @NotNull
  @Contract(value = "null, !null -> param2; !null, null -> param1", pure = true)
  static <V> UnaryOperator<V> andThen(UnaryOperator<V> first, UnaryOperator<V> after) {
    if (first == null && after != null) {
      return after;
    } else if (after == null && first != null) {
      return first;
    } else if (after == null) {
      return UnaryOperator.identity();
    }
    return t -> after.apply(first.apply(t));
  }

  static <V> Function<V, V> compose(Function<V, V> first, Function<V, V> after) {
    if (first == null && after != null) {
      return after;
    } else if (after == null && first != null) {
      return first;
    } else if (after == null) {
      return UnaryOperator.identity();
    }
    return t -> after.apply(first.apply(t));
  }


}
