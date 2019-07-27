package sarf.automation.poi.calc.changehandler.poi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

@Data
public class RetrievedTracker {

  private final Set<Sheet> sheets = new HashSet<>();
  private final Map<Sheet, Row> rows = new HashMap<>();
  private final Map<Row, Cell> cells = new HashMap<>();

  public RetrievedTracker add(Sheet sheet) {
    sheets.add(sheet);
    return this;
  }

  public RetrievedTracker add(Row row) {
    rows.put(row.getSheet(), row);
    return this;
  }

  public RetrievedTracker add(Cell cell) {
    cells.put(cell.getRow(), cell);
    return this;
  }


}
