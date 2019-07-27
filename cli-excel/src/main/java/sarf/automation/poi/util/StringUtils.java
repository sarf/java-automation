package sarf.automation.poi.util;

import static sarf.automation.poi.util.FunctionUtils.map;
import static sarf.automation.poi.util.FunctionUtils.optMap;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import lombok.NonNull;

public interface StringUtils {

  static Integer parseInt(String str) {
    return optParseInt(str)
        .orElse(null);
  }

  static Optional<Integer> optParseInt(String str) {
    return optMap(str, s -> parse(s, Integer::parseInt));
  }

  static <T, U, E extends NumberFormatException> U parse(
      T value, RuntimeExceptionFunction<T, U, NumberFormatException> parseFunc) {
    try{
      return map(value, parseFunc);
    } catch(NumberFormatException ex) {
      return null;
    }
  }

  static boolean isEmpty(String str) {
    return str == null || str.isEmpty();
  }

  static boolean isNotEmpty(String str) {
    return str != null && str.isEmpty();
  }

  static String toStackTrace(@NonNull Throwable t) {
    StringWriter out = new StringWriter();
    t.printStackTrace(new PrintWriter(out));
    return out.toString();
  }

  static Optional<String> opt(String offset) {
    return isEmpty(offset) ? Optional.empty() : Optional.of(offset);
  }
}
