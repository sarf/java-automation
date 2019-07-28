package sarf.commons.util;

import static sarf.commons.util.EmptyUtil.isNotEmpty;
import static sarf.commons.util.OptionalUtil.toOpt;
import static sarf.commons.util.StreamUtil.streamFrom;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CollectionUtil {

  static <T extends Collection<U>, U> Optional<T> collOpt(T entity) {
    return isNotEmpty(entity) ? toOpt(entity) : Optional.empty();
  }

  public static <T extends Collection<U>, R extends Collection<U>, U> T addAll(T dest, R source) {
    if (dest != null && isNotEmpty(source)) {
      dest.addAll(source);
    }
    return dest;
  }

  public static <T extends Collection<U>, R extends Stream<U>, U> T addAll(T dest, R source) {
    if (dest != null && source != null) {
      source.forEach(dest::add);
    }
    return dest;
  }

  @SafeVarargs
  public static <T extends Collection<U>, U> T addAll(T dest, U... source) {
    if (dest != null && isNotEmpty(source)) {
      dest.addAll(Arrays.asList(source));
    }
    return dest;
  }

  @SafeVarargs
  static <T extends Collection<U>, U> T cross(Supplier<T> supplierCollection,
      Stream<U>... streams) {
    return streamFrom(streams).flatMap(s -> s).collect(Collectors.toCollection(supplierCollection));
  }

}
