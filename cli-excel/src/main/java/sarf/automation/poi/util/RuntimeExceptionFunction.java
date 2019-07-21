package sarf.automation.poi.util;

import java.util.function.Function;

@FunctionalInterface
public interface RuntimeExceptionFunction<T, R, E extends RuntimeException>
    extends Function<T, R> {

  R applyThrows(T t) throws E;

  @Override
  default R apply(T t) {
    return applyThrows(t);
  }
}
