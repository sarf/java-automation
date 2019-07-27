package sarf.automation.poi.calc.changehandler;

import static sarf.automation.poi.calc.CalcUtils.columnStringToIndex;
import static sarf.automation.poi.util.CollectionUtils.indexValid;
import static sarf.automation.poi.util.FunctionUtils.optFilter;
import static sarf.automation.poi.util.FunctionUtils.optMap;
import static sarf.automation.poi.util.ObjectUtils.isAnyNull;
import static sarf.automation.poi.util.StringUtils.isEmpty;
import static sarf.automation.poi.util.StringUtils.parseInt;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import sarf.automation.poi.calc.SheetHandler;
import sarf.automation.poi.util.PatternUtils.EasyMatcher;
import sarf.automation.poi.util.StringUtils;

public interface CellHelper {

  static final Pattern ONLY_NUMBER = Pattern.compile("(\\d+)");
  Pattern COLUMN_ROW = Pattern.compile("([A-Z]*)([0-9]*)");
  Pattern ROW_COLUMN = Pattern.compile("(\\d*)[^\\d]+(\\d*)");

  static Sheet getSheet(SheetHandler sheetHandler, String sheet) {
    return findSheet(sheetHandler, sheet)
         .or(() -> getSheetByName(sheetHandler, sheet))
        .or(() -> getSheetByNumber(sheetHandler, sheet))
        .orElse(null);
  }

  static Optional<Sheet> findSheet(SheetHandler sheetHandler, String sheet) {
    if(sheet.toLowerCase().startsWith("sheet:")) {
      Pattern p = Pattern.compile(sheet.substring(6));
      return sheetHandler.getSheets().stream()
                         .filter(s -> p.matcher(s.getSheetName()).find())
                         .findAny();
    }
    return Optional.empty();
  }

  static Optional<Sheet> getSheetByName(SheetHandler sheetHandler, String sheet) {
    return optMap(sheet, sheetHandler.getByName()::get);
  }

  static Optional<Sheet> getSheetByNumber(SheetHandler sheetHandler, String sheet) {
    return optMap(sheet, StringUtils::parseInt)
        .map(i -> getSheetAsNumber(sheetHandler, i))
        .or(() -> optMap(EasyMatcher.of(ONLY_NUMBER.matcher(sheet))
                                    .find()
                                    .toGroup(1), s -> getSheetAsNumber(sheetHandler, s)));
  }

  static Sheet getSheetAsNumber(SheetHandler sheetHandler, String sheetNumber) {
    return getSheetAsNumber(sheetHandler, parseInt(sheetNumber));
  }

  private static Sheet getSheetAsNumber(SheetHandler sheetHandler, Integer sheetNumber) {
    if (sheetNumber == null) {
      return null;
    }
    List<Sheet> sheets = sheetHandler.getSheets();
    return optFilter(sheetNumber, indexValid(sheets))
        .map(sheets::get)
        .orElse(null);
  }

  static Cell getCellByExact(Sheet sheet, int row, int column) {
    return Optional.ofNullable(sheet.getRow(row))
            .map(r -> r.getCell(column))
            .orElse(null);
  }

  static Cell getCell(Sheet sheet, String cell) {
    if (isAnyNull(sheet, cell) || isEmpty(cell)) {
      return null;
    }
    return Optional.ofNullable(getCellByColumnRow(sheet, cell))
                   .orElseGet(() -> getCellByRowColumn(sheet, cell));
  }


  static Cell getCellByColumnRow(Sheet sheet, String cell) {
    if (cell == null) {
      return null;
    }
    Matcher matcher = COLUMN_ROW.matcher(cell);
    return optFilter(matcher, Matcher::find)
        .map(m -> getCellByString(sheet, m.group(2), m.group(1), false))
        .orElse(null);
  }

  static Cell getCellByRowColumn(Sheet sheet, String cell) {
    if (cell == null) {
      return null;
    }
    Matcher matcher = ROW_COLUMN.matcher(cell);
    return optFilter(matcher, Matcher::find)
        .map(m -> getCellByString(sheet, m.group(1), m.group(2), true))
        .orElse(null);
  }

  static Cell getCellByString(Sheet sheet, String row, String column, boolean zeroBased) {
    if (isAnyNull(sheet, row, column)) {
      return null;
    }
    ZeroBased<Integer> mappedRow = ZeroBased.of(StringUtils.parseInt(row), zeroBased);
    ZeroBased<Integer> mappedColumn = mapColumnToInteger(column, zeroBased);
    return getCellByInteger(sheet, mappedRow, mappedColumn);
  }

  static ZeroBased<Integer> mapColumnToInteger(String c, boolean zeroBased) {
    return optFilter(ZeroBased.of(columnStringToIndex(c), true), s -> s.entity >= 0)
        .orElseGet(() -> ZeroBased.of(parseInt(c), zeroBased));
  }

  static Cell getCellByInteger(Sheet sheet, ZeroBased<Integer> row, ZeroBased<Integer> column) {
    if (isAnyNull(sheet, row, column)) {
      return null;
    }
    try {
      return optFilter(row, s -> s.entity >= 0)
          .map(ZeroBased::adjustedNumberZeroBased)
          .map(o -> o.orElse(null))
          .map(Number::intValue)
          .map(sheet::getRow)
          .map(r -> column.adjustedNumberZeroBased()
                          .map(n -> r.getCell(n.intValue()))
                          .orElse(null))
          .orElse(null);
    } catch (NullPointerException ex) {
      return null;
    }
  }

  @Data(staticConstructor = "of")
  class ZeroBased<T> {

    private final T entity;
    private final boolean zeroBased;

    public <U> ZeroBased<U> map(Function<T, U> convert) {
      return of(convert.apply(getEntity()), isZeroBased());
    }

    private Optional<Number> adjustedNumberZeroBased() {
      if (entity instanceof Number) {
        long adjustment = 0;
        if (!zeroBased) {
          adjustment = -1;
        }
        return Optional.of(((Number) entity).longValue() + adjustment);
      }
      return Optional.empty();
    }

  }
}
