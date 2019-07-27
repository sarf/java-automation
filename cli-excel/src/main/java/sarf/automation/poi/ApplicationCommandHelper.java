package sarf.automation.poi;

import static java.util.Collections.emptyList;
import static sarf.automation.poi.commands.CommandHelper.editCommand;
import static sarf.automation.poi.commands.CommandHelper.findCommand;
import static sarf.automation.poi.util.CollectionUtils.addAll;
import static sarf.automation.poi.util.FunctionUtils.defaultValue;
import static sarf.automation.poi.util.StreamUtils.streamFrom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import sarf.automation.poi.commands.Command;
import sarf.automation.poi.commands.CommandHelper;
import sarf.automation.poi.commands.CommandOptions;

public class ApplicationCommandHelper {

  private ApplicationCommandHelper() {
    throw new UnsupportedOperationException();
  }

  static List<Command> getAllCommands() {
    return Arrays.asList(editCommand(), findCommand());
  }

  static final List<Function<List<String>, CommandOptions>> commandOptions = Arrays
      .asList(CommandHelper::getEditCommand, CommandHelper::getFindCommand);

  public static ApplicationCommands extractApplicationCommands(List<String> unstableArgs, String input, String output,
      Command... starting) {
    List<CommandOptions> commandOptions = getAllCommandOptions(unstableArgs, ApplicationCommandHelper::getCommands);
    List<Command> commands = commandOptions.stream().map(CommandOptions::getCommand)
                                           .filter(Objects::nonNull)
                                           .collect(Collectors.toCollection(ArrayList::new));
    addAll(commands, starting);
    return ApplicationCommands.builder()
                              .input(input)
                              .output(defaultValue(output, input))
                              .commands(commands)
                              .commandOptions(commandOptions)
                              .build();
  }

  private static List<CommandOptions> getAllCommandOptions(List<String> unstableArgs,
      Function<List<String>, CommandOptions> func) {
    List<CommandOptions> commands = new ArrayList<>();
    CommandOptions command = func.apply(unstableArgs);
    int stableSize = -1;
    while (command != null && stableSize != unstableArgs.size()) {
      stableSize = unstableArgs.size();
      commands.add(command);
      command = func.apply(unstableArgs);
    }
    return commands;
  }

  private static CommandOptions getCommands(List<String> unstableArgs) {
    for (Function<List<String>, CommandOptions> c : commandOptions) {
      CommandOptions apply = c.apply(unstableArgs);
      if (apply != null) {
        return apply;
      }
    }
    return null;
  }

  static String first(Collection<String> removeOption) {
    return streamFrom(removeOption)
        .findFirst()
        .orElse(null);
  }

  static Function<List<String>, List<String>> removeFirst() {
    return l -> l.isEmpty() ? emptyList() : Collections.singletonList(l.remove(0));
  }

}
