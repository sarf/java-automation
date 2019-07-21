package sarf.automation.poi;

import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationTest {

  @Test
  public void testMain() {
    Application.Main("input.xsv", "-edit", "Sheet1!AB", "7", "--outputFile", "output.xsv", "-edit", "1", "A", "to", "Gurka");
  }

}