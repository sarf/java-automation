package sarf.commons8.util;

import lombok.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static sarf.commons8.util.CollectionUtil.collOpt;

public interface StreamUtil {

    @NonNull
    @SafeVarargs
    static <T> Stream<T> streamFrom(T... args) {
        if (args == null || args.length <= 0) {
            return Stream.empty();
        }
        return Stream.of(args);
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
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }

    static <T> Stream<T> streamFrom(Stream<T> args) {
        if (args == null) {
            return Stream.empty();
        }
        return args;
    }


}
