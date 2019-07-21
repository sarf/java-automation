package sarf.automation.poi.calc;

import static sarf.automation.poi.util.StringUtils.isEmpty;

public class CalcUtils {

  private CalcUtils() {

  }

  final static int rowFactor = 25;

  public static int rowStringToIndex(String row) {
    if (isEmpty(row)) {
      return -1;
    }
    int sum = row.length() > 1 ? (int) Math.pow(rowFactor, row.length() - 1) : 0;
    for (int i = row.length() - 1; i >= 0; i--) {
      int curValue = row.charAt(row.length() - i - 1) - 'A';
      sum += (curValue) * Math.pow(rowFactor, i);
    }
    return sum;
  }


}
