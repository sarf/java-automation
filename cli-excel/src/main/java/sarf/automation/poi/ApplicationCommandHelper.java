package sarf.automation.poi;

import static java.util.Collections.emptyList;
import static sarf.automation.poi.util.CollectionUtils.isEmpty;
import static sarf.automation.poi.util.FunctionUtils.defaultValue;
import static sarf.automation.poi.util.PredicateUtils.never;
import static sarf.automation.poi.util.StreamUtils.streamFrom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sarf.automation.poi.ApplicationCommands.Command;
import sarf.automation.poi.util.PredicateUtils;

public class ApplicationCommandHelper {

  static final List<Function<List<String>, Command>> commands = Arrays
     .asList(ApplicationCommandHelper::getEditCommand, ApplicationCommandHelper::getFindCommand);

  public static ApplicationCommands extractApplicationCommands(List<String> unstableArgs, String input, String output) {
    List<Command> commands = new ArrayList<>(getAllCommands(unstableArgs, ApplicationCommandHelper::getCommands));
    return ApplicationCommands.builder()
                                               .input(input)
                                               .output(defaultValue(output, input))
                                               .commands(commands)
                                               .build();
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

  private static Command getCommands(List<String> unstableArgs) {
    for (Function<List<String>, Command> c : commands) {
      Command apply = c.apply(unstableArgs);
      if (apply != null) {
        return apply;
      }
    }
    return null;
  }

  static Command getEditCommand(List<String> unstableArgs) {
    List<String> list = removeOption(unstableArgs, s -> emptyList(), optionMatchers("-edit", "-set"),
                      stopAfter(s -> s.equals("to"))
                                 .or(stopBefore(s -> s.startsWith("-"))));
    if (isEmpty(list)) {
      return null;
    }
    return new Command("edit", filter(list, PredicateUtils.eq("to").negate()));
  }

  static Command getFindCommand(List<String> unstableArgs) {
    List<String> list = removeOption(unstableArgs, s -> emptyList(), optionMatchers("-find"),
                                                 stopAfter(s -> s.equals("to"))
                                                            .or(stopBefore(s -> s.startsWith("-"))));
    if (isEmpty(list)) {
      return null;
    }
    return new Command("find", filter(list, PredicateUtils.eq("to").negate()));
  }

  static List<String> removeOption(List<String> mutableList,
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

  static int calculateRemoveIndex(List<String> mutableList,
     BiPredicate<List<String>, Integer> stopRemove, int i) {
   int removeIndex = i + 1;
   while (removeIndex < mutableList.size() - 1 && !stopRemove.test(mutableList, removeIndex)) {
     removeIndex++;
   }
   return removeIndex;
 }

  static String first(Collection<String> removeOption) {
    return streamFrom(removeOption)
        .findFirst()
        .orElse(null);
  }

  static Function<List<String>, List<String>> removeFirst() {
    return l -> l.isEmpty() ? emptyList() : Collections.singletonList(l.remove(0));
  }

  static Predicate<String> optionMatchers(String... optionNames) {
    return Stream.of(optionNames)
                 .filter(Objects::nonNull)
                 .map(s -> s.startsWith("-") ? s : "-" + s)
                 .map(ApplicationCommandHelper::equalsIgnorePredicate)
                 .reduce(Predicate::or)
                 .orElse(never());
  }

  private static Predicate<String> equalsIgnorePredicate(String s) {
    return s::equalsIgnoreCase;
  }

  static BiPredicate<List<String>, Integer> stopAfter(Predicate<String> str) {
    return (list, index) -> index > 0 && index < list.size() - 1 && str.test(list.get(index - 1));
  }

  static BiPredicate<List<String>, Integer> stopBefore(Predicate<String> str) {
    return (list, index) -> index > 0 && index < list.size() - 1 && str.test(list.get(index + 1));
  }

  static <T> List<T> filter(Collection<T> coll, Predicate<T> predicate) {
    return streamFrom(coll)
        .filter(predicate)
        .collect(Collectors.toList());
  }
}
