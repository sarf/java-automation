package sarf.commons8.util;

import java.util.Optional;

public interface OptionalUtil {

  static <T> Optional<T> toOpt(T entity) {
    return Optional.ofNullable(entity);
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  static <T> Optional<T> or(Optional<T> a, Optional<T> b) {
    if (a.isPresent()) {
      return a;
    }
    return b;
  }

}
