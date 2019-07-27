package sarf.automation.poi.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ArrayUtils {

  @SafeVarargs
  static <T> boolean isEmpty(T... args) {
    return (args == null || args.length <= 0);
  }

  @SafeVarargs
  static <T> boolean isNotEmpty(T... args) {
    return (args != null && args.length > 0);
  }

  @SafeVarargs
  static <T> Optional<T[]> toOpt(T... args) {
    if(isEmpty(args)) return Optional.empty();
    return Optional.of(args);
  }

  @SafeVarargs
  static <T> List<T> toList(T... args) {
    return toOpt(args)
        .map(Arrays::asList)
        .orElseGet(Collections::emptyList);
  }
}
