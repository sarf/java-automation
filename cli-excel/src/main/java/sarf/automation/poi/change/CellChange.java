package sarf.automation.poi.change;

import lombok.Data;

@Data
public abstract class CellChange {

  private final String sheet;
  private final String cell;

  public abstract String describe();



}
