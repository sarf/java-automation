package sarf.automation.poi.calc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import lombok.Data;
import lombok.NonNull;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import sarf.automation.poi.calc.changehandler.ChangeHandler;
import sarf.automation.poi.calc.changehandler.FindCellChangeHandler;
import sarf.automation.poi.calc.changehandler.TextCellChangeHandler;
import sarf.automation.poi.change.CellChange;

@Data
public class WorkFile {

  private static final Logger logger = Logger.getLogger(WorkFile.class.getName());

  @NonNull
  private final File workFile;

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

  @SuppressWarnings("unchecked")
  public <T extends CellChange> Collection<T> handleChange(T change) {
    if (change == null) {
      return Collections.emptyList();
    }
    ChangeHandler changeHandler = changeHandlers.get(change.getClass());
    if (changeHandler != null) {
      logger.info(change::describe);
      return changeHandler.perform(this, change);
    } else {
      logger.warning(() -> String
          .format("did not find a change handler for change %s - change class %s", change, change.getClass()));
    }
    return Collections.emptySet();
  }

  private static final Map<Class<? extends CellChange>, ChangeHandler> changeHandlers =
      new HashMap<>();

  private static <T extends CellChange> void registerChangeHandler(ChangeHandler<T> changeHandler) {
    changeHandlers.put(changeHandler.getChangeHandled(), changeHandler);
  }

  static {
    registerChangeHandler(new TextCellChangeHandler());
    registerChangeHandler(new FindCellChangeHandler());
  }

}
