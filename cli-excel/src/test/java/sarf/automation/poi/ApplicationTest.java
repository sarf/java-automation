package sarf.automation.poi;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Test;

public class ApplicationTest {

  @Test
  public void testMain() throws IOException {
    Application.main("-input", "input.xsv", "-edit", "Sheet1!AB", "7", "--outputFile", "output.xsv", "-find", "Sheet:A?", "Dag i månad", "8;8;9;0;1", "-edit", "1", "A", "to", "Gurka");
  }

}