package sarf.automation.poi.change;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import sarf.automation.poi.ApplicationCommands.Command;

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

  public static String getCommandName() {
    return "edit";
  }
  public static TextCellChange toTextCellChange(Command command) {
    String sheet = command.getStrings().get(0);
    String cell;
    String[] sheetSplit = sheet.split("!");
    if (sheetSplit.length > 1) {
      sheet = sheetSplit[0];
      cell = sheetSplit[1];
    } else {
      cell = command.getStrings().get(1);
    }
    String value = ChangeFactory.getLast(command.getStrings());
    return new TextCellChange(sheet, cell, value);
  }

  @Override
  public String describe() {
    return String.format("Sets cell at %s to %s%s.", getCell(), getValue(), isShouldTransform() ? " (will be transformed into correct value as appropriate)" : "");
  }
}
