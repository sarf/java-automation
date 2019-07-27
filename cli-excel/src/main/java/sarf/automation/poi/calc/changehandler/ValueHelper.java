package sarf.automation.poi.calc.changehandler;

import static sarf.automation.poi.util.FunctionUtils.optFilter;
import static sarf.automation.poi.util.FunctionUtils.optMap;

import java.util.Date;
import java.util.Optional;
import sarf.automation.poi.util.StringUtils;

public interface ValueHelper {

  static Optional<Number> toDouble(String value) {
    return optFilter(value, v -> v.contains("."))
        .map(Double::parseDouble);
  }

  static Optional<Number> toInt(String value) {
    return optMap(value, StringUtils::parseInt);
  }

  static Optional<Boolean> toBoolean(String value) {
    return optMap(value, Boolean::parseBoolean);
  }

  static Optional<Date> toDate(String value) {
    return Optional.empty();
  }
}
