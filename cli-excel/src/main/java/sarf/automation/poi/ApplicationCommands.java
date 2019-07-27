package sarf.automation.poi;

import static sarf.automation.poi.ApplicationCommandHelper.getAllCommands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import sarf.automation.poi.commands.Command;
import sarf.automation.poi.commands.CommandOptions;

@Builder
@Data
public class ApplicationCommands {

  private final String input;

  private final String output;

  @NonNull
  private final List<Command> commands;

  @NonNull
  private final List<CommandOptions> commandOptions;

  public String getHelp() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("Commands:%n"));
    Set<Command> unique = new HashSet<>(commands);
    unique.addAll(getAllCommands());
    unique.forEach(c -> sb.append(c.getHelp()));
    return sb.toString();
  }

  public List<String> getOptions(Command command) {
    return commandOptions.stream().filter(s -> command.equals(s.getCommand()))
                         .map(CommandOptions::getOptions)
                         .findAny()
                         .orElse(null);
  }


}
