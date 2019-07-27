package sarf.automation.poi.calc.changehandler;

import static org.junit.Assert.assertEquals;
import static sarf.automation.poi.calc.CalcUtils.columnFactorOther;
import static sarf.automation.poi.calc.CalcUtils.cellLocationToString;

import org.junit.Test;

public class CellHelperTest {


  @Test
  public void notWorking() {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < columnFactorOther; i++) {
      String substring = cellLocationToString(i, 0).substring(0, 1);
      sb.append(substring);
    }
    assertEquals("", sb.toString());
  }

  @Test
  public void qcellLocationToString() {
    assertEquals("Z2", cellLocationToString(columnFactorOther-1,1));
    assertEquals("A2", cellLocationToString(0,1));
    assertEquals("AA2", cellLocationToString(columnFactorOther,1));
    assertEquals("AAAA2", cellLocationToString(columnFactorOther * columnFactorOther * columnFactorOther,1));
    assertEquals("C2", cellLocationToString(2,1));
    assertEquals("AAA6", cellLocationToString(columnFactorOther * columnFactorOther,5));

  }


}