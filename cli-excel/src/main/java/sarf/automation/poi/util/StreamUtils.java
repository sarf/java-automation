package sarf.automation.poi.util;

import java.util.Collection;
import java.util.stream.Stream;

public interface StreamUtils {

  @SafeVarargs
  static <T> Stream<T> streamFrom(T... args) {
    if(args == null || args.length <= 0) return Stream.empty();
    return Stream.of(args);
  }

  static <T> Stream<T> streamFrom(Collection<T> args) {
    if(args == null || args.isEmpty()) return Stream.empty();
    return args.stream();
  }

  static <T> Stream<T> streamFrom(Stream<T> args) {
    if(args == null) return Stream.empty();
    return args;
  }

}
