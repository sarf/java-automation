package sarf.commons.util;

import static sarf.commons.util.EmptyUtil.isNotEmpty;
import static sarf.commons.util.OptionalUtil.toOpt;

import java.util.Collection;
import java.util.Optional;

public interface CollectionUtil {

  static <T extends Collection<U>, U> Optional<T> collOpt(T entity) {
    return isNotEmpty(entity) ? toOpt(entity) : Optional.empty();
  }

}
