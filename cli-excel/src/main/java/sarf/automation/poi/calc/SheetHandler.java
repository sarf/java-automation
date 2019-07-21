package sarf.automation.poi.calc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;
import org.apache.poi.ss.usermodel.Sheet;

@Data
public class SheetHandler {
  @NonNull
  private final Map<String, Sheet> byName;

  @NonNull
  private final List<Sheet> sheets;


  public SheetHandler(
      @NonNull Map<String, Sheet> byName,
      @NonNull List<Sheet> sheets) {
    this.byName = Collections.unmodifiableMap(byName);
    this.sheets = Collections.unmodifiableList(sheets);
  }

  public SheetHandler(@NonNull List<Sheet> sheets) {
    this(sheets.stream().collect(Collectors.toMap(Sheet::getSheetName, Function.identity())), sheets);
  }

  public static SheetHandler from(Iterator<Sheet> iterator) {
    List<Sheet> list = new ArrayList<>();
    iterator.forEachRemaining(list::add);
    return new SheetHandler(list);
  }
}
