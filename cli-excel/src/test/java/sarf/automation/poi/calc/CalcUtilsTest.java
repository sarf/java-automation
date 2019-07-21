package sarf.automation.poi.calc;

import static org.junit.Assert.*;
import static sarf.automation.poi.calc.CalcUtils.rowFactor;
import static sarf.automation.poi.calc.CalcUtils.rowStringToIndex;

import org.junit.Test;

public class CalcUtilsTest {

  @Test
  public void rowStringToIndexSingle() {
    assertEquals(rowFactor, 'Z' - 'A');
    assertEquals(0, rowStringToIndex("A"));
    assertEquals(rowFactor, rowStringToIndex("AA"));
    assertEquals(rowFactor + 1, rowStringToIndex("AB"));
    assertEquals(rowFactor * rowFactor, rowStringToIndex("AAA"));
  }

}