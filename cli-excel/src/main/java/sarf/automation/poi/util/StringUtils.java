package sarf.automation.poi.util;

import static sarf.automation.poi.util.FunctionUtils.map;
import static sarf.automation.poi.util.FunctionUtils.optMap;

import java.util.Optional;

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
}
