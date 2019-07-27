package sarf.automation.poi.commands;

import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class Command {
  private final String name;
  @NonNull
  private final List<String> options;

  final int minArguments;
  final int maxArguments;
  final String example;

  public Command(String name, @NonNull List<String> options, String example) {
    this(name, options, 1, 1, example);
  }
  public Command(String name, @NonNull List<String> options, int minArguments, String example) {
    this(name, options, minArguments, minArguments, example);
  }

  public Command(String name, @NonNull List<String> options, int minArguments, int maxArguments, String example) {
    this.name = name;
    this.options = options;
    this.minArguments = minArguments;
    this.maxArguments = maxArguments;
    this.example = String.format(example);
  }

  public String getHelp() {
    String arguments = minArguments == maxArguments ? String.valueOf(minArguments) : String.format("%d-%d", minArguments, maxArguments);
    String pluralArguments = minArguments == 1 && maxArguments == 1 ? "" : "s";
    return String.format("> %s => %s%n  needs %s argument%s%n   Example:%n%s%n", name, options, arguments,
                         pluralArguments, example);
  }
}
