package sarf.commons12.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

class Constants {

  static final Predicate always = any -> true;
  static final Predicate never = any -> false;
  static final Consumer nopConsumer = c -> {
  };
    static final Supplier nullSupplier = () -> null;

  private Constants() {
    throw new UnsupportedOperationException();
  }
}
