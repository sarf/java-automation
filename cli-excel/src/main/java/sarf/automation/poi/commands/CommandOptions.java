package sarf.automation.poi.commands;

import java.util.List;
import lombok.Data;

@Data
public class CommandOptions {
  private final Command command;
  private final List<String> options;
}
