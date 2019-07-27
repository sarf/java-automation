package sarf.automation.poi.calc.changehandler;

import static sarf.automation.poi.calc.changehandler.CellHelper.getSheet;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import sarf.automation.poi.calc.WorkFile;
import sarf.automation.poi.change.CellChange;
import sarf.automation.poi.change.TextCellChange;

public class TextCellChangeHandler<T> extends ChangeHandlerBase<TextCellChange> {

  public TextCellChangeHandler() {
    super(TextCellChange.class);
  }

  @Override
  public Collection<CellChange> performActual(WorkFile workFile, TextCellChange change) {
    Sheet sheet = getSheet(workFile.getSheetHandler(), change.getSheet());

    // Cell is contained so it can be changed as it goes along
    Contained<Cell> cell = new Contained<>(CellHelper.getCell(sheet, change.getCell()));

    splitToStream(change.getValue())
        .forEach(value -> editCellAndAdvance(value, cell, change.isShouldTransform()));
    return Collections.emptySet();
  }

  @Data
  @AllArgsConstructor
  private static class Contained<T> {

    private T entity;
  }

  private void editCellAndAdvance(String value, Contained<Cell> cell, boolean shouldTransform) {
    if (cell.entity != null) {
      Cell c = cell.entity;
      setCellValue(cell.entity, value, shouldTransform);
      cell.entity = c.getRow().getCell(c.getColumnIndex() + 1);
    }

  }

  private void setCellValue(Cell d, String value, boolean shouldTransform) {
    d.setCellValue(value);
    if (shouldTransform) {
      ValueHelper.toBoolean(value).ifPresent(d::setCellValue);
      ValueHelper.toDouble(value)
                 .or(() -> ValueHelper.toInt(value))
                 .ifPresent(n -> d.setCellValue(n.doubleValue()));
      ValueHelper.toDate(value).ifPresent(d::setCellValue);
    }
  }

  private static Stream<String> splitToStream(String value) {
    return splitToStream(value, ";");
  }

  private static Stream<String> splitToStream(String value, String regexSeperator) {
    return Stream.of(value.split(regexSeperator));
  }


}
