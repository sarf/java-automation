package sarf.commons12.util;

import org.jetbrains.annotations.Contract;

import java.util.Collection;

@SuppressWarnings("WeakerAccess")
public class EmptyUtil {

  private EmptyUtil() {
    throw new UnsupportedOperationException();
  }

  @Contract(value = "null -> true", pure = true)
  @SafeVarargs
  static <T> boolean isEmpty(T... args) {
    return (args == null || args.length <= 0);
  }

  @Contract(value = "null -> false", pure = true)
  @SafeVarargs
  static <T> boolean isNotEmpty(T... args) {
    return (args != null && args.length > 0);
  }

  @Contract(value = "null -> true", pure = true)
  public static <T extends Collection<U>, U> boolean isEmpty(T collection) {
    return collection == null || collection.isEmpty();
  }

  @Contract(value = "null -> false", pure = true)
  public static <T extends Collection<U>, U> boolean isNotEmpty(T collection) {
    return !isEmpty(collection);
  }


}
