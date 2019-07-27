package sarf.automation.poi.util;

import static sarf.automation.poi.util.ArrayUtils.toOpt;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public interface StreamUtils {

  @SafeVarargs
  static <T> Stream<T> streamFrom(T... args) {
    return toOpt(args)
        .stream()
        .flatMap(Stream::of);
  }

  static <T> Stream<T> streamFrom(Collection<T> args) {
    return CollectionUtils.toOpt(args)
                          .stream()
                          .flatMap(Collection::stream);
  }

  static <T> Stream<T> streamFrom(Stream<T> args) {
    if (args == null) {
      return Stream.empty();
    }
    return args;
  }

}
