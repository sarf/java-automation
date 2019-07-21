package sarf.automation.poi.util;

import java.util.function.Function;

@FunctionalInterface
public interface ExceptionFunction<T, R, E extends Exception> extends Function<T, R> {

  R applyThrows(T t) throws E;

  @Override
  default R apply(T t) {
    try {
      return applyThrows(t);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
