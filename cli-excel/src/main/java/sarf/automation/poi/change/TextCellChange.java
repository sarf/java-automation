package sarf.automation.poi.change;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TextCellChange extends CellChange {

  private final String value;
  private final boolean shouldTransform;


  public TextCellChange(String sheet, String cell, String value) {
    this(sheet, cell, value, true);
  }

  public TextCellChange(String sheet, String cell, String value, boolean shouldTransform) {
    super(sheet, cell);
    this.value = value;
    this.shouldTransform = shouldTransform;
  }

}
