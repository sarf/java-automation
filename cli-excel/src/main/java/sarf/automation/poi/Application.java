package sarf.automation.poi;

import static java.util.Collections.emptyList;
import static sarf.automation.poi.util.CollectionUtils.isEmpty;
import static sarf.automation.poi.util.FunctionUtils.defaultValue;
import static sarf.automation.poi.util.PredicateUtils.never;
import static sarf.automation.poi.util.StreamUtils.streamFrom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sarf.automation.poi.ApplicationCommands.Command;
import sarf.automation.poi.calc.WorkFile;
import sarf.automation.poi.change.ChangeFactory;

public class Application {

  private static final Logger logger = Logger.getLogger(Application.class.getName());

  public static void main(String... args) throws IOException {
    List<String> unstableArgs = streamFrom(args)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(ArrayList::new));
    String input = getInput(unstableArgs);
    String output = getOutput(unstableArgs);
    List<Command> commands = new ArrayList<>(getAllCommands(unstableArgs, Application::getEditCommand));
    ApplicationCommands c = ApplicationCommands.builder()
                                               .input(input)
                                               .output(defaultValue(output, input))
                                               .commands(commands)
                                               .build();

    WorkFile workFile = new WorkFile(new File(input));

    workFile.openFile();
    c.getCommands().stream()
     .map(ChangeFactory::from)
     .filter(Objects::nonNull)
     .forEach(workFile::handleChange);

    File outputFile = new File(output);
    File backupFile = new File(outputFile.getAbsolutePath() + ".backup");
    if (outputFile.exists()) {
      if (backupFile.exists()) {
        delete(backupFile);
      }
      move(outputFile, backupFile);
    }
    try {
      workFile.saveFile(outputFile);
    } catch (IOException ex) {
      if (backupFile.exists()) {
        move(backupFile, outputFile);
      }
    }
  }

  private static void delete(File f) {
    try {
      Files.deleteIfExists(f.toPath());
    } catch (IOException e) {
      logger.warning(() -> String.format("Failed to delete %s due to %s", f, e));
    }
  }

  private static void move(File source, File dest) {
    try {
      Files.move(source.toPath(), dest.toPath());
    } catch (IOException e) {
      logger.warning(() -> String.format("Failed to move %s to %s due to %s", source, dest, e));
    }
  }

  private static List<Command> getAllCommands(List<String> unstableArgs, Function<List<String>, Command> func) {
    List<Command> commands = new ArrayList<>();
    Command command = func.apply(unstableArgs);
    while (command != null) {
      commands.add(command);
      command = func.apply(unstableArgs);
    }
    return commands;
  }

  private static Command getEditCommand(List<String> unstableArgs) {
    List<String> list = removeOption(unstableArgs, s -> emptyList(), optionMatchers("-edit", "-set"),
                                     stopAfter(s -> s.equals("to"))
                                         .or(stopBefore(s -> s.startsWith("-"))));
    if (isEmpty(list)) {
      return null;
    }
    return new Command("edit", filter(list, eq("to").negate()));
  }

  private static Predicate<String> eq(String s) {
    return s::equals;
  }

  private static <T> List<T> filter(Collection<T> coll, Predicate<T> predicate) {
    return streamFrom(coll)
        .filter(predicate)
        .collect(Collectors.toList());
  }

  private static String getOutput(List<String> unstableArgs) {
    return first(
        removeOption(unstableArgs, s -> emptyList(), optionMatchers("-outputFile", "-outputFile"), immediately));
  }

  private static String getInput(List<String> unstableArgs) {
    return first(removeOption(unstableArgs, removeFirst(), optionMatchers("-input", "-inputFile"), immediately));
  }

  private static String first(Collection<String> removeOption) {
    return streamFrom(removeOption)
        .findFirst()
        .orElse(null);
  }

  private static BiPredicate<List<String>, Integer> immediately = (a, b) -> true;

  private static Function<List<String>, List<String>> removeFirst() {
    return l -> l.isEmpty() ? emptyList() : Collections.singletonList(l.remove(0));
  }

  private static Predicate<String> optionMatchers(String... optionNames) {
    return Stream.of(optionNames)
                 .filter(Objects::nonNull)
                 .map(s -> s.startsWith("-") ? s : "-" + s)
                 .map(Application::equalsIgnorePredicate)
                 .reduce(Predicate::or)
                 .orElse(never());
  }

  private static Predicate<String> equalsIgnorePredicate(String s) {
    return s::equalsIgnoreCase;
  }

  private static BiPredicate<List<String>, Integer> stopAfter(Predicate<String> str) {
    return (list, index) -> index > 0 && index < list.size() - 1 && str.test(list.get(index - 1));
  }

  private static BiPredicate<List<String>, Integer> stopBefore(Predicate<String> str) {
    return (list, index) -> index > 0 && index < list.size() - 1 && str.test(list.get(index + 1));
  }

  private static List<String> removeOption(List<String> mutableList,
      Function<List<String>, List<String>> removeIfNotFound,
      Predicate<String> optionIndicator, BiPredicate<List<String>, Integer> stopRemove) {
    for (int i = 0; i < mutableList.size(); i++) {
      String str = mutableList.get(i);
      if (optionIndicator.test(str) || (str.startsWith("-") && optionIndicator.test(str.substring(1)))) {
        int removeIndex = calculateRemoveIndex(mutableList, stopRemove, i);
        if (removeIndex < mutableList.size()) {
          List<String> value = new ArrayList<>();
          while (i < removeIndex) {
            value.add(mutableList.remove(i + 1));
            removeIndex--;
          }
          mutableList.remove(i);
          return value;
        }
      }
    }
    return removeIfNotFound.apply(mutableList);
  }

  private static int calculateRemoveIndex(List<String> mutableList,
      BiPredicate<List<String>, Integer> stopRemove, int i) {
    int removeIndex = i + 1;
    while (removeIndex < mutableList.size() - 1 && !stopRemove.test(mutableList, removeIndex)) {
      removeIndex++;
    }
    return removeIndex;
  }

}
