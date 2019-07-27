package sarf.automation.poi;

import static java.util.Collections.emptyList;
import static sarf.automation.poi.util.FunctionUtils.defaultValue;
import static sarf.automation.poi.util.StreamUtils.streamFrom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import sarf.automation.poi.ApplicationCommands.Command;
import sarf.automation.poi.calc.WorkFile;
import sarf.automation.poi.change.CellChange;
import sarf.automation.poi.change.ChangeFactory;
import sarf.automation.poi.util.time.InterruptAfter;
import sarf.automation.poi.util.time.TimeoutData;

public class Application {

  private static final Logger logger = Logger.getLogger(Application.class.getName());

  public static void main(String... args) throws IOException {
    List<String> unstableArgs = streamFrom(args)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(ArrayList::new));
    String input = getInput(unstableArgs);
    String output = getOutput(unstableArgs);
    ApplicationCommands c = ApplicationCommandHelper.extractApplicationCommands(unstableArgs, input, output);

    WorkFile workFile = new WorkFile(new File(input));

    workFile.openFile();
    Set<CellChange> changes = c.getCommands().stream()
                               .map(ChangeFactory::from)
                               .filter(Objects::nonNull)
                               .collect(Collectors.toSet());
    handleChanges(workFile::handleChange, changes);

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

  private static <T extends CellChange>  void handleChangesAbort(Function<T, Collection<T>> handleChange, Set<T> changes) {
    InterruptAfter.interruptAfter(new TimeoutData(2, TimeUnit.MINUTES), () -> handleChanges(handleChange, changes));

  }

  private static <T extends CellChange> void handleChanges(Function<T, Collection<T>> handleChange, Set<T> changes) {

    Set<T> currentChanges = changes;

    while(!currentChanges.isEmpty()) {
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

  private static String getOutput(List<String> unstableArgs) {
    return ApplicationCommandHelper.first(
        ApplicationCommandHelper
            .removeOption(unstableArgs, s -> emptyList(), ApplicationCommandHelper
                .optionMatchers("-outputFile", "-outputFile"), immediately));
  }

  private static String getInput(List<String> unstableArgs) {
    return ApplicationCommandHelper.first(ApplicationCommandHelper
                     .removeOption(unstableArgs, ApplicationCommandHelper.removeFirst(), ApplicationCommandHelper
                         .optionMatchers("-input", "-inputFile"), immediately));
  }

  static BiPredicate<List<String>, Integer> immediately = (a, b) -> true;

}
