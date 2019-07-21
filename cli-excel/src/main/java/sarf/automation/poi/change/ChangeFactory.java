package sarf.automation.poi.change;

import static sarf.automation.poi.util.CollectionUtils.isEmpty;

import java.util.List;
import sarf.automation.poi.ApplicationCommands.Command;

public interface ChangeFactory {

  static CellChange from(Command command) {
    if (command == null) {
      return null;
    }
    if ("edit".equalsIgnoreCase(command.getName()) && command.getStrings().size() > 1) {
      return toTextCellChange(command);
    }
    return null;
  }

  static TextCellChange toTextCellChange(Command command) {
    String sheet = command.getStrings().get(0);
    String cell;
    String[] sheetSplit = sheet.split("!");
    if(sheetSplit.length > 1) {
      sheet = sheetSplit[0];
      cell = sheetSplit[1];
    } else {
      cell = command.getStrings().get(1);
    }
    String value = getLast(command.getStrings());
    return new TextCellChange(sheet, cell, value);
  }

  static <T> T getLast(List<T> strings) {
    if (isEmpty(strings)) {
      return null;
    }
    return strings.get(strings.size() - 1);
  }

}
