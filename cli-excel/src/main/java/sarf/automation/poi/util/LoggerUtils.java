package sarf.automation.poi.util;

import static sarf.automation.poi.util.FunctionUtils.doIfNNS;
import static sarf.automation.poi.util.PredicateUtils.always;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;

public interface LoggerUtils {

  static <T> T supply(@NonNull Supplier<T> supplier, @NonNull Predicate<T> shouldLog, Runnable log) {
    T t = supplier.get();
    if(log != null && shouldLog.test(t)) {
      log.run();
    }
    return t;
  }

  static <T> T supply(@NonNull Supplier<T> supplier, Runnable log) {
    return supply(supplier, always(), log);
  }

  static <T> Consumer<T> log(Supplier<Logger> logger, Supplier<Level> level, Function<T, String> messageMaker) {
    return o -> doIfNNS(logger, l -> doIfNNS(level, e -> l.log(e, () -> messageMaker.apply(o))));
  }

  static <T> Consumer<T> log(Supplier<Logger> logger, Level level, Function<T, String> messageMaker) {
    return o -> doIfNNS(logger, l -> l.log(level, () -> messageMaker.apply(o)));
  }

  static <T> Consumer<T> info(Supplier<Logger> logger, Function<T, String> messageMaker) {
    return log(logger, Level.INFO, messageMaker);
  }

  static <T> Consumer<T> fine(Supplier<Logger> logger, Function<T, String> messageMaker) {
    return log(logger, Level.FINE, messageMaker);
  }

  static <T> Consumer<T> finer(Supplier<Logger> logger, Function<T, String> messageMaker) {
    return log(logger, Level.FINER, messageMaker);
  }

  static <T> Consumer<T> finest(Supplier<Logger> logger, Function<T, String> messageMaker) {
    return log(logger, Level.FINEST, messageMaker);
  }

  static <T> Consumer<T> severe(Supplier<Logger> logger, Function<T, String> messageMaker) {
    return log(logger, Level.SEVERE, messageMaker);
  }

  static <T> Consumer<T> warning(Supplier<Logger> logger, Function<T, String> messageMaker) {
    return log(logger, Level.WARNING, messageMaker);
  }


}
