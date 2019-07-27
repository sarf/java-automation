package sarf.automation.poi.calc;

import static org.junit.Assert.*;
import static sarf.automation.poi.calc.CalcUtils.columnFactor;
import static sarf.automation.poi.calc.CalcUtils.columnStringToIndex;

import org.junit.Test;

public class CalcUtilsTest {

  @Test
  public void reverse() {
    assertEquals("W", CalcUtils.fromColumnIndexToString(22));
  }

  @Test
  public void rowStringToIndexSingle() {
    assertEquals(columnFactor, 'Z' - 'A');
    assertEquals(0, columnStringToIndex("A"));
    assertEquals(columnFactor, columnStringToIndex("AA"));
    assertEquals(columnFactor + 1, columnStringToIndex("AB"));
    assertEquals(columnFactor * columnFactor, columnStringToIndex("AAA"));
  }

}