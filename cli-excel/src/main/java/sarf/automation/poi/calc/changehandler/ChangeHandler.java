package sarf.automation.poi.calc.changehandler;

import sarf.automation.poi.calc.WorkFile;
import sarf.automation.poi.change.CellChange;

public interface ChangeHandler<T extends CellChange> {

  default void perform(WorkFile workFile, CellChange change) {
    if (getChangeHandled().isInstance(change)) {
      performActual(workFile, getChangeHandled().cast(change));
    }
  }

  void performActual(WorkFile workFile, T change);

  Class<T> getChangeHandled();

}
