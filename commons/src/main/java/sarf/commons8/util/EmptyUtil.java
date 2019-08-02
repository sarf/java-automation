package sarf.commons8.util;

import java.util.Collection;

@SuppressWarnings("WeakerAccess")
public class EmptyUtil {

  private EmptyUtil() {
    throw new UnsupportedOperationException();
  }

  @SafeVarargs
  static <T> boolean isEmpty(T... args) {
    return (args == null || args.length <= 0);
  }

  @SafeVarargs
  static <T> boolean isNotEmpty(T... args) {
    return (args != null && args.length > 0);
  }

  public static <T extends Collection<U>, U> boolean isEmpty(T collection) {
    return collection == null || collection.isEmpty();
  }

  public static <T extends Collection<U>, U> boolean isNotEmpty(T collection) {
    return !isEmpty(collection);
  }


}
