package sarf.automation.poi.calc;

import static sarf.automation.poi.util.FunctionUtils.consumeRecycle;
import static sarf.automation.poi.util.FunctionUtils.doOnce;
import static sarf.automation.poi.util.FunctionUtils.toConsumer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Logger;
import lombok.Data;
import lombok.NonNull;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
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
    updateSheets(sheetHandler.getRetrievedTracker().getSheets());
    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
      workbook.write(fos);
    }
  }

  private void updateSheets(Set<Sheet> sheets) {
    FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
    final AtomicBoolean hadFormulas = new AtomicBoolean(false);
    Runnable hadFormula = doOnce(handleHadFormulas(hadFormulas));
    boolean hadError = sheets.stream()
                             .map(SheetHandler::sheetToCells)
                             .flatMap(Function.identity())
                             .filter(c -> CellType.FORMULA.equals(c.getCellType()))
                             .map(s -> consumeRecycle(s, toConsumer(hadFormula)))
                             .map(formulaEvaluator::evaluateFormulaCell)
                             .anyMatch(CellType.ERROR::equals);
    if (hadError) {
      logger.warning(() -> String.format("Some formulas in the sheets could not be updated.%n%s",
                                         "The document will now trigger client application update on open."));
      workbook.setForceFormulaRecalculation(true);
    }
  }

  private Runnable handleHadFormulas(AtomicBoolean hadFormulas) {
    return () -> {
      hadFormulas.set(true);
      logger.info("Some of the sheets edited had formulas, attempting to update them.");
    };
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
