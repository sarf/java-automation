package sarf.automation.poi.calc.changehandler;

import static sarf.automation.poi.calc.CalcUtils.cellLocationToString;
import static sarf.automation.poi.calc.SheetHandler.sheetToCells;
import static sarf.automation.poi.calc.changehandler.CellHelper.getCell;
import static sarf.automation.poi.calc.changehandler.CellHelper.getCellByExact;
import static sarf.automation.poi.util.FunctionUtils.optMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import sarf.automation.poi.calc.SheetHandler;
import sarf.automation.poi.calc.WorkFile;
import sarf.automation.poi.change.CellChange;
import sarf.automation.poi.change.FindCellChange;
import sarf.automation.poi.change.TextCellChange;
import sarf.automation.poi.util.PredicateUtils;
import sarf.automation.poi.util.StringUtils;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FindCellChangeHandler extends ChangeHandlerBase<FindCellChange> {

  public FindCellChangeHandler() {
    super(FindCellChange.class);
  }

  @Override
  public Collection<CellChange> performActual(WorkFile workFile, FindCellChange change) {
    Sheet sheet = CellHelper.getSheet(workFile.getSheetHandler(), change.getSheet());
    TextCellChange textCellChange = Optional.ofNullable(findCell(sheet, change.getSoughtCell()))
                                            .map(cell -> getAdjustedCell(sheet, cell, change.getOffset()))
                                            .map(cell -> new TextCellChange(change.getSheet(),
                                                                            cellLocationToString(cell),
                                                                            change.getValue()))
                                            .orElse(null);
    if (textCellChange != null) {
      return Collections.singleton(textCellChange);
    } else {
      return Collections.emptySet();
    }
  }

  private Cell getAdjustedCell(Sheet sheet, Cell cell, String offset) {
    return StringUtils.opt(offset)
                      .map(o -> optMap(sheet, s -> getCell(s, o)).orElse(null))
                      .map(a -> getCellByExact(sheet, cell.getRowIndex() + a.getRowIndex(),
                                               cell.getColumnIndex() + a.getColumnIndex()))
                      .orElse(cell);
  }

  private Cell findCell(Sheet sheet, String soughtCell) {
    Predicate<Cell> first = PredicateUtils.notNull();
    Collection<Predicate<Cell>> predicates = createPredicates(first, soughtCell);
    return predicates.stream()
                     .map(p -> sheetToCells(sheet).filter(p))
                     .flatMap(s -> s)
                     .findAny()
                     .orElse(null);
    //return findCell(sheet, predicates).findAny().orElse(null);
  }

  private Stream<Cell> findCell(Sheet sheet, Collection<Predicate<Cell>> predicates) {
    int lastRowNum = sheet.getLastRowNum();

    for (Predicate<Cell> predicate : predicates) {
      for (int index = sheet.getFirstRowNum(); index < lastRowNum; index++) {
        Row row = sheet.getRow(index);
        short lastCellNum = row.getLastCellNum();
        for (short colIndex = row.getFirstCellNum(); colIndex < lastCellNum; colIndex++) {
          Cell cell = row.getCell(colIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
          if (predicate.test(cell)) {
            return Stream.of(cell);
          }
        }
      }
    }
    return Stream.empty();
  }

  private static Collection<Predicate<Cell>> createPredicates(Predicate<Cell> mandatory, String soughtCell) {
    Pattern pattern = safeCompile(soughtCell);
    return Stream.of(cellPredicate(soughtCell::equals),
                     cellPredicate(soughtCell::equalsIgnoreCase),
                     cellPredicate(s -> pattern.matcher(s).matches()),
                     cellPredicate(s -> pattern.matcher(s).find()),
                     cellPredicate(soughtCell::contains))
                 .map(mandatory::and)
                 .collect(Collectors.toSet());
  }

  private static Pattern safeCompile(String soughtCell) {
    try {
      return Pattern.compile(soughtCell);
    } catch (PatternSyntaxException ex) {
      return Pattern.compile("A{999391809}");
    }

  }

  private static Predicate<Cell> cellPredicate(Predicate<String> predicate) {
    return c -> Optional.ofNullable(c)
                        .filter(s -> CellType.STRING.equals(s.getCellType()))
                        .map(Cell::getStringCellValue)
                        .filter(predicate::test)
                        .isPresent();
  }
}
