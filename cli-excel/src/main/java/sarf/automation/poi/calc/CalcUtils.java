package sarf.automation.poi.calc;

import static sarf.automation.poi.util.StringUtils.isEmpty;

import org.apache.poi.ss.usermodel.Cell;

public class CalcUtils {

  private CalcUtils() {

  }

  public final static int columnFactor = 25;
  public final static int columnFactorOther = 26; ///  don't ask

  public static int columnStringToIndex(String row) {
    if (isEmpty(row)) {
      return -1;
    }
    int sum = row.length() > 1 ? (int) Math.pow(columnFactor, row.length() - 1) : 0;
    for (int i = row.length() - 1; i >= 0; i--) {
      int curValue = row.charAt(row.length() - i - 1) - 'A';
      sum += (curValue) * Math.pow(columnFactor, i);
    }
    return sum;
  }


  public static String cellLocationToString(Cell cell) {
    return cellLocationToString(cell.getColumnIndex(), cell.getRowIndex());
  }

  public static String cellLocationToString(int columnIndex, int rowNum) {
    String colStr = fromColumnIndexToString(columnIndex);
    return String.format("%s%d", colStr, rowNum + 1);
  }

  public static String fromColumnIndexToString(int columnIndex) {
    int column = columnIndex;
    StringBuilder sb = new StringBuilder();
    if (column < columnFactorOther) {
      addReminder(column+1, sb);
    } else {
      while (column >= columnFactorOther) {
        int nextColumn = column / (columnFactorOther);
        addColumn(column, sb);
        column = nextColumn;
      }
      addColumn(column, sb);
    }
    return sb.toString();
  }

  public static void addColumn(int column, StringBuilder sb) {
    addReminder(column % (columnFactor), sb);
  }

  public static void addReminder(int column, StringBuilder sb) {
    char remainder = (char) (64 + column);
    if (sb.length() == 0) {
      sb.append(remainder);
    } else {
      sb.append(remainder);
      //sb.insert(0, remainder);
    }
  }
}
