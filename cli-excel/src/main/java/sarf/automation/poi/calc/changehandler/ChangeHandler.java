package sarf.automation.poi.calc.changehandler;

import java.util.Collection;
import java.util.Collections;
import sarf.automation.poi.calc.WorkFile;
import sarf.automation.poi.change.CellChange;

public interface ChangeHandler<T extends CellChange> {

  default Collection<CellChange> perform(WorkFile workFile, T change) {
    if (getChangeHandled().isInstance(change)) {
      return performActual(workFile, getChangeHandled().cast(change));
    }
    return Collections.emptySet();
  }

  Collection<CellChange> performActual(WorkFile workFile, T change);

  Class<T> getChangeHandled();

}
