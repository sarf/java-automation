package sarf.automation.poi.calc.changehandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import sarf.automation.poi.change.CellChange;

@AllArgsConstructor
@Data
public abstract class ChangeHandlerBase<T extends CellChange> implements ChangeHandler<T> {

  @Getter
  private final Class<T> changeHandled;

}
