package sarf.automation.poi.change;

import static sarf.automation.poi.util.StringUtils.isEmpty;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import sarf.automation.poi.ApplicationCommands.Command;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FindCellChange extends CellChange {

  private final String soughtCell;
  private final String value;
  private final String offset;

  public FindCellChange(String sheet, String cell, String soughtCell, String offset, String value) {
    super(sheet, cell);
    this.soughtCell = soughtCell;
    this.offset = offset;
    this.value = value;
  }

  @Override
  public String describe() {
    return String.format("Searches for %s%s and set values using TextCellChange to %s", getSoughtCell(),
                         isEmpty(offset) ? "" : ", offsetting it by " + offset, getValue());
  }

  public static String getCommandName() {
    return "find";
  }

  public static FindCellChange toFindCellChange(Command command) {
    List<String> strings = command.getStrings();
    String sheet = strings.get(0);
    String offset = "";
    int valueIndex = 2;
    if(strings.size() > valueIndex + 1) {
      offset = strings.get(valueIndex);
      valueIndex++;
    }
    return new FindCellChange(sheet, null, strings.get(1), offset, strings.get(valueIndex));
  }
}
