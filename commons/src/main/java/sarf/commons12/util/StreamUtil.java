package sarf.commons12.util;

import lombok.NonNull;

import java.util.Collection;
import java.util.stream.Stream;

import static sarf.commons12.util.CollectionUtil.collOpt;

public interface StreamUtil {

  @NonNull
  @SafeVarargs
  static <T> Stream<T> streamFrom(T... args) {
    return Stream.ofNullable(args).flatMap(Stream::of);
  }

  static <T extends Collection<U>, U> Stream<U> streamFrom(T args) {
    return collOpt(args)
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
