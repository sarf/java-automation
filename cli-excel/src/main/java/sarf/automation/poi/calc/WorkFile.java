package sarf.automation.poi.calc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NonNull;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import sarf.automation.poi.calc.changehandler.ChangeHandler;
import sarf.automation.poi.calc.changehandler.TextCellChangeHandler;
import sarf.automation.poi.change.CellChange;

@Data
public class WorkFile {

  @NonNull private final File workFile;

  private Workbook workbook;
  private SheetHandler sheetHandler;

  public void openFile() throws IOException {
    this.workbook = WorkbookFactory.create(workFile);
    this.sheetHandler = SheetHandler.from(workbook.sheetIterator());
  }

  public void saveFile(File outputFile) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
      workbook.write(fos);
    }
  }

  public <T extends CellChange> void handleChange(T change) {
    if (change == null) return;
    changeHandlers.get(change.getClass())
        .perform(this, change);
  }

  private static final Map<Class<? extends CellChange>, ChangeHandler> changeHandlers =
      new HashMap<>();

  private static <T extends CellChange> void registerChangeHandler(ChangeHandler<T> changeHandler) {
    changeHandlers.put(changeHandler.getChangeHandled(), changeHandler);
  }

  static {
    registerChangeHandler(new TextCellChangeHandler());
  }

}
