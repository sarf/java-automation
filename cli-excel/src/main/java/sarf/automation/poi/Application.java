package sarf.automation.poi;

import static java.util.Collections.emptyList;
import static sarf.automation.poi.ApplicationCommandHelper.extractApplicationCommands;
import static sarf.automation.poi.ApplicationCommandHelper.first;
import static sarf.automation.poi.commands.CommandHelper.optionMatchers;
import static sarf.automation.poi.ApplicationCommandHelper.removeFirst;
import static sarf.automation.poi.commands.CommandHelper.removeOption;
import static sarf.automation.poi.util.StreamUtils.streamFrom;
import static sarf.automation.poi.util.StringUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import sarf.automation.poi.commands.Command;
import sarf.automation.poi.calc.WorkFile;
import sarf.automation.poi.change.CellChange;
import sarf.automation.poi.change.ChangeFactory;
import sarf.automation.poi.commands.CommandHelper;
import sarf.automation.poi.util.time.InterruptAfter;
import sarf.automation.poi.util.time.TimeoutData;

public class Application {

  private static final Logger logger = Logger.getLogger(Application.class.getName());

  public static void main(String... args) throws IOException {
    List<String> unstableArgs = streamFrom(args)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(ArrayList::new));
    Command inputCommand = CommandHelper.createInput();
    Command outputCommand = CommandHelper.createOutput();
    String input = getInput(inputCommand.getOptions(), unstableArgs);
    String output = getOutput(outputCommand.getOptions(), unstableArgs);
    ApplicationCommands c = extractApplicationCommands(unstableArgs, input, output, inputCommand, outputCommand);

    if(isEmpty(c.getInput())) {
      logger.severe(() -> String.format("Failure(4): User must specify input file.%n%s", c.getHelp()));
      System.exit(4);
      return;
    }


    File inputFile = new File(c.getInput());
    WorkFile workFile = new WorkFile(inputFile);

    try {
      workFile.openFile();
    } catch (IOException ex) {
      logger.warning(() -> String.format("Failure(1): Could not open %s due to %s.", inputFile, ex));
      System.exit(1);
    }
    Set<CellChange> allChanges = getAllChanges(c);
    if(allChanges.isEmpty()) {
        logger.severe(() -> String.format("Failure(5): User must specify at least one command.%n%s", c.getHelp()));
      System.exit(5);
        return;
    }
    handleChanges(workFile::handleChange, allChanges);

    File outputFile = new File(c.getOutput());
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
      logger.warning(() -> String.format("Failure (2): Could not save to %s due to %s.%s", outputFile, ex,
                                         backupFile.exists() ? "%nAttempting to restore previous backup file..." : ""));
      Runtime.getRuntime().addShutdownHook(new Thread(() -> System.exit(2)));
      if (backupFile.exists()) {
        move(backupFile, outputFile);
      }
    }
  }

  public static Set<CellChange> getAllChanges(ApplicationCommands c) {
    return c.getCommands().stream()
            .map(s -> ChangeFactory.from(s, c.getOptions(s)))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
  }

  private static <T extends CellChange> void handleChangesAbort(Function<T, Collection<T>> handleChange,
      Set<T> changes) {
    InterruptAfter.interruptAfter(new TimeoutData(2, TimeUnit.MINUTES), () -> handleChanges(handleChange, changes));

  }

  private static <T extends CellChange> void handleChanges(Function<T, Collection<T>> handleChange, Set<T> changes) {

    Set<T> currentChanges = changes;

    while (!currentChanges.isEmpty()) {
      currentChanges = currentChanges.stream()
                                     .map(handleChange)
                                     .flatMap(Collection::stream)
                                     .collect(Collectors.toSet());
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

  private static String getOutput(List<String> optionNames, List<String> unstableArgs) {
    return first(
        removeOption(unstableArgs, s -> emptyList(), optionMatchers(optionNames), immediately));
  }

  private static String getInput(List<String> optionNames, List<String> unstableArgs) {
    return first(removeOption(unstableArgs, removeFirst(),
                              optionMatchers(optionNames), immediately));
  }

  static BiPredicate<List<String>, Integer> immediately = (a, b) -> true;

}
