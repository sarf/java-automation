package sarf.automation.poi.calc;

import static sarf.automation.poi.util.CollectionUtils.indexValid;
import static sarf.automation.poi.util.FunctionUtils.consumeRecycle;
import static sarf.automation.poi.util.FunctionUtils.map;
import static sarf.automation.poi.util.FunctionUtils.optFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import sarf.automation.poi.calc.changehandler.poi.RetrievedTracker;

@Data
@Getter(AccessLevel.PROTECTED)
public class SheetHandler {

  @NonNull
  private final Map<String, Sheet> byName;

  @NonNull
  private final List<Sheet> sheets;

  private final RetrievedTracker retrievedTracker = new RetrievedTracker();

  public SheetHandler(
      @NonNull Map<String, Sheet> byName,
      @NonNull List<Sheet> sheets) {
    this.byName = Collections.unmodifiableMap(byName);
    this.sheets = Collections.unmodifiableList(sheets);
  }

  public SheetHandler(@NonNull List<Sheet> sheets) {
    this(sheets.stream().collect(Collectors.toMap(Sheet::getSheetName, Function.identity())), sheets);
  }

  public Sheet getByName(String name) {
    return track(byName.get(name));
  }

  public Sheet findSheet(Predicate<Sheet> predicate) {
    return track(sheets.stream()
                       .filter(predicate)
                       .findAny()
                       .orElse(null));
  }

  private Sheet track(Sheet any) {
    return Optional.ofNullable(any)
                   .map(s -> consumeRecycle(s, retrievedTracker::add))
                   .orElse(null);
  }

  public static SheetHandler from(Iterator<Sheet> iterator) {
    List<Sheet> list = new ArrayList<>();
    iterator.forEachRemaining(list::add);
    return new SheetHandler(list);
  }

  public Sheet getSheetByIndex(Integer index) {
    return track(optFilter(index, indexValid(getSheets()))
                     .map(sheets::get)
                     .orElse(null));
  }

  public static Stream<Cell> sheetToCells(Sheet sheet) {
    int lastRowNum = sheet.getLastRowNum();
    List<Row> rows = new ArrayList<>();
    for (int index = sheet.getFirstRowNum(); index < lastRowNum; index++) {
      rows.add(sheet.getRow(index));
    }
    return rows.stream().flatMap(SheetHandler::rowToCells);
  }

  public static Stream<Cell> rowToCells(Row row) {
    short lastCellNum = row.getLastCellNum();
    List<Cell> cells = new ArrayList<>();
    for (short colIndex = row.getFirstCellNum(); colIndex < lastCellNum; colIndex++) {
      map(row.getCell(colIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL), cells::add);
    }
    return cells.stream();
  }
}
