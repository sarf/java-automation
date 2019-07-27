package sarf.automation.poi.commands;

import static java.util.Collections.emptyList;
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
import sarf.automation.poi.util.PredicateUtils;
import sarf.automation.poi.util.StreamUtils;

public class CommandHelper {

  public static final List<String> EDIT_OPTION_NAMES = List.of("-edit", "-set");
  public static final List<String> FIND_OPTION_NAMES = Collections.singletonList("-find");

  public static Command editCommand() {
    return new Command("edit", EDIT_OPTION_NAMES, 3, 4,
                       "  -edit Sheet:Juli W22 to 4381%n  -set Sheet!1 CAD89 blä%n  -edit Sheet!Arne FUL1 to false"
                           + "%nNOTE: Sheet:<...> will attempt to search using the text as a regular expression.");
  }

  public static Command findCommand() {
    return new Command("find", FIND_OPTION_NAMES, 3, 4,
                       "  -find Sheet:Juni \"Dag i månad\" [offset] <value>   (offset = row,column offset)%n"
                           + "NOTE: The text is searched for in a few ways:%n1. as written. 2. case-insensitive.%n"
                           + "3. regexp that matches all text. 4. regexp that matches some test.%n"
                           + "5. being part of the cell text.");
  }

  public static CommandOptions getEditCommand(List<String> unstableArgs) {
    List<String> list = removeOption(unstableArgs, s -> emptyList(), optionMatchers(EDIT_OPTION_NAMES),
                                     stopAfter(s -> s.equals("to"))
                                         .or(stopBefore(s -> s.startsWith("-"))));
    if (list.isEmpty()) {
      return null;
    }
    return new CommandOptions(editCommand(),
                              filter(list, PredicateUtils
                                  .eq("to").negate()));
  }

  public static CommandOptions getFindCommand(List<String> unstableArgs) {
    List<String> list = removeOption(unstableArgs, s -> emptyList(), optionMatchers(FIND_OPTION_NAMES),
                                     stopAfter(s -> s.equals("to"))
                                         .or(stopBefore(s -> s.startsWith("-"))));
    if (list.isEmpty()) {
      return null;
    }
    return new CommandOptions(findCommand(),
                              filter(list, PredicateUtils.eq("to").negate()));
  }

  public static List<String> removeOption(List<String> mutableList,
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

  public static Predicate<String> optionMatchers(String... optionNames) {
    return optionMatchers(Stream.of(optionNames));
  }

  public static Predicate<String> optionMatchers(Collection<String> collection) {
    return optionMatchers(StreamUtils.streamFrom(collection));
  }

  static Predicate<String> optionMatchers(Stream<String> optionNames) {
    return optionNames
        .filter(Objects::nonNull)
        .map(s -> s.startsWith("-") ? s : "-" + s)
        .map(CommandHelper::equalsIgnorePredicate)
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

  public static Command createSimple(String input, String s, String s2, String s3) {
    return new Command(input, Arrays.asList(s, s2), s3);
  }

  public static Command createInput() {
    return createSimple("input", "-input", "-inputFile", "  -input ABC.xlsx");
  }

  public static Command createOutput() {
    return createSimple("output", "-output", "-outputFile",
                        "  -outputFile DEST.xlsx%nOutput will default to input if not specified");
  }
}
