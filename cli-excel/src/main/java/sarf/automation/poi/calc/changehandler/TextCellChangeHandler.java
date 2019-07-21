package sarf.automation.poi.calc.changehandler;

import static sarf.automation.poi.calc.CalcUtils.rowStringToIndex;
import static sarf.automation.poi.util.CollectionUtils.indexValid;
import static sarf.automation.poi.util.FunctionUtils.execute;
import static sarf.automation.poi.util.FunctionUtils.optFilter;
import static sarf.automation.poi.util.FunctionUtils.optMap;
import static sarf.automation.poi.util.ObjectUtils.isAnyNull;
import static sarf.automation.poi.util.StringUtils.parseInt;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import sarf.automation.poi.calc.SheetHandler;
import sarf.automation.poi.calc.WorkFile;
import sarf.automation.poi.change.TextCellChange;
import sarf.automation.poi.util.FunctionUtils;
import sarf.automation.poi.util.PatternUtils.EasyMatcher;
import sarf.automation.poi.util.StringUtils;

public class TextCellChangeHandler extends ChangeHandlerBase<TextCellChange> {

  public TextCellChangeHandler() {
    super(TextCellChange.class);
  }

  private Sheet getSheet(SheetHandler sheetHandler, String sheet) {
    return getSheetByName(sheetHandler, sheet)
        .or(() -> getSheetByNumber(sheetHandler, sheet))
        .orElse(null);
  }

  private Optional<Sheet> getSheetByName(SheetHandler sheetHandler, String sheet) {
    return optMap(sheet, sheetHandler.getByName()::get);
  }

  private static final Pattern ONLY_NUMBER = Pattern.compile("(\\d+)");

  private Optional<Sheet> getSheetByNumber(SheetHandler sheetHandler, String sheet) {
    return optMap(sheet, StringUtils::parseInt)
        .map(i -> getSheetAsNumber(sheetHandler, i))
        .or(() -> optMap(EasyMatcher.of(ONLY_NUMBER.matcher(sheet))
                                    .find()
                                    .toGroup(1), s -> getSheetAsNumber(sheetHandler, s)));
  }


  private Sheet getSheetAsNumber(SheetHandler sheetHandler, String sheetNumber) {
    return getSheetAsNumber(sheetHandler, parseInt(sheetNumber));
  }

  private Sheet getSheetAsNumber(SheetHandler sheetHandler, Integer sheetNumber) {
    if (sheetNumber == null) {
      return null;
    }
    List<Sheet> sheets = sheetHandler.getSheets();
    return optFilter(sheetNumber, indexValid(sheets))
        .map(sheets::get)
        .orElse(null);
  }

  @Override
  public void performActual(WorkFile workFile, TextCellChange change) {
    Sheet sheet = getSheet(workFile.getSheetHandler(), change.getSheet());
    Contained<Cell> cell = new Contained<>(getCell(sheet, change.getCell()));
    multipleChanges(change.getValue())
        .forEach(value -> editCellAndAdvance(value, cell, change.isShouldTransform()));
  }

  @AllArgsConstructor
  @Data
  private static class Contained<T> {

    private T entity;
  }

  private void editCellAndAdvance(String value, Contained<Cell> cell, boolean shouldTransform) {
    Optional.ofNullable(cell.entity)
            .map(c -> FunctionUtils.execute(cell.entity, d -> setCellValue(d, value, shouldTransform)))
            .ifPresent(c -> cell.entity = c.getRow().getCell(c.getColumnIndex() + 1));

  }

  private void setCellValue(Cell d, String value, boolean shouldTransform) {
    if (shouldTransform) {
      execute(value, d::setCellValue);
      toBoolean(value).ifPresent(d::setCellValue);
      toDouble(value)
          .or(() -> toInt(value))
          .ifPresent(n -> d.setCellValue(n.doubleValue()));
      toDate(value).ifPresent(d::setCellValue);
    }
  }

  private Optional<Number> toDouble(String value) {
    return optFilter(value, v -> v.contains("."))
        .map(Double::parseDouble);
  }

  private Optional<Number> toInt(String value) {
    return optMap(value, StringUtils::parseInt);
  }

  private static Optional<Boolean> toBoolean(String value) {
    return optMap(value, Boolean::parseBoolean);
  }

  private static Optional<Date> toDate(String value) {
    return Optional.empty();
  }

  private Stream<String> multipleChanges(String value) {
    return Stream.of(value.split(";"));
  }

  private static final Pattern COLUMN_ROW = Pattern.compile("([A-Z]*)([0-9]*)");
  private static final Pattern ROW_COLUMN = Pattern.compile("(\\d*)[^\\d]+(\\d*)");

  private Cell getCell(Sheet sheet, String cell) {
    if (isAnyNull(sheet, cell)) {
      return null;
    }
    return Optional.ofNullable(getCellByColumnRow(sheet, cell))
                   .orElseGet(() -> getCellByRowColumn(sheet, cell));
  }

  private Cell getCellByColumnRow(Sheet sheet, String cell) {
    if (cell == null) {
      return null;
    }
    Matcher matcher = COLUMN_ROW.matcher(cell);
    return optFilter(matcher, Matcher::find)
        .map(m -> getCellByString(sheet, m.group(2), m.group(1)))
        .orElse(null);
  }

  private Cell getCellByRowColumn(Sheet sheet, String cell) {
    if (cell == null) {
      return null;
    }
    Matcher matcher = ROW_COLUMN.matcher(cell);
    return optFilter(matcher, Matcher::find)
        .map(m -> getCellByString(sheet, m.group(1), m.group(2)))
        .orElse(null);
  }

  private Cell getCellByString(Sheet sheet, String row, String column) {
    if (isAnyNull(sheet, row, column)) {
      return null;
    }
    ZeroBased<Integer> mappedRow = ZeroBased.of(StringUtils.parseInt(row), false);
    ZeroBased<Integer> mappedColumn = mapColumnToInteger(column);
    return getCellByInteger(sheet, mappedRow, mappedColumn);
  }

  private ZeroBased<Integer> mapColumnToInteger(String c) {
    return optFilter(ZeroBased.of(rowStringToIndex(c), true), s -> s.entity >= 0)
        .orElseGet(() -> ZeroBased.of(parseInt(c), false));
  }

  @Data(staticConstructor = "of")
  private static class ZeroBased<T> {

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

  private Cell getCellByInteger(Sheet sheet, ZeroBased<Integer> row, ZeroBased<Integer> column) {
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


}
