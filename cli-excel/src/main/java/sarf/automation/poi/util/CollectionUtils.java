package sarf.automation.poi.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionUtils {

  static <T> boolean isEmpty(Collection<T> collection) {
    return collection == null || collection.isEmpty();
  }

  static <T> boolean isNotEmpty(Collection<T> collection) {
    return !isEmpty(collection);
  }

  static <T> Predicate<Integer> indexValid(List<T> list) {
    return indexValid(0, list.size());
  }

  static <T extends Number> Predicate<T> indexValid(T min, T max) {
    return i -> i.longValue() >= min.longValue() && i.longValue() <= max.longValue();
  }

}
