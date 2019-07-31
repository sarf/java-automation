package sarf.commons12.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface FunctionUtil {

  @SuppressWarnings("unchecked")
  static <T> Consumer<T> nopConsumer() {
    return Constants.nopConsumer;
  }

  @SuppressWarnings("unchecked")
  static <T> Supplier<T> nopSupplier() {
    return Constants.nopSupplier;
  }


}
