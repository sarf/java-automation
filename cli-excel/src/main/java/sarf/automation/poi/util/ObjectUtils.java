package sarf.automation.poi.util;

import java.util.Objects;

public interface ObjectUtils {

  @SafeVarargs
  static <T> boolean isAnyNull(T... objects) {
    return PredicateUtils.isAny(Objects::isNull, objects);
  }

  @SafeVarargs
  static <T> boolean isAllNull(T... objects) {
    return PredicateUtils.isAll(Objects::isNull, objects);
  }

}
