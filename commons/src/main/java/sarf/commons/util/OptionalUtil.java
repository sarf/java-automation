package sarf.commons.util;

import java.util.Optional;

public interface OptionalUtil {

  static <T> Optional<T> toOpt(T entity) {
    return Optional.ofNullable(entity);
  }

}
