package sarf.commons.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

interface Constants {

  Predicate always = any -> true;
  Predicate never = any -> false;

  Consumer nopConsumer = c -> {
  };
  Supplier nopSupplier = () -> null;
}
