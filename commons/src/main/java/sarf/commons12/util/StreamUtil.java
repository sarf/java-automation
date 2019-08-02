package sarf.commons12.util;

import lombok.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static sarf.commons12.util.CollectionUtil.collOpt;

public interface StreamUtil {

  @NonNull
  @SafeVarargs
  static <T> Stream<T> streamFrom(T... args) {
    return Stream.ofNullable(args).flatMap(Stream::of);
  }

    @NonNull
    static <T> Stream<T> streamFrom(Iterable<T> iterable) {
        if (iterable == null) {
            return Stream.empty();
        }
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    @NonNull
    static <T> Stream<T> streamFrom(Iterator<T> iterator) {
        if (iterator == null) {
            return Stream.empty();
        }
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
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
