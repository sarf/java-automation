package sarf.automation.poi.change;

import static sarf.automation.poi.util.CollectionUtils.findInFirstCome;
import static sarf.automation.poi.util.CollectionUtils.isEmpty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import sarf.automation.poi.ApplicationCommands.Command;

public class ChangeFactory {

  private ChangeFactory() {
    throw new UnsupportedOperationException(getClass() + " is mot allowed to instantiate");
  }

  private static final Map<String, Class<?>> CHANGE_MAP = getAllCellChanges();

  // TODO: do this with CDI or whatnot
  private static Map<String, Class<?>> getAllCellChanges() {
    Map<String, Class<?>> map = new HashMap<>();
    for (Class<?> clazz : Arrays.asList(FindCellChange.class, TextCellChange.class)) {
      map.put(getCommandName(clazz), clazz);
    }
    return map;
  }

  static String getCommandName(Class<?> clazz) {
    return Stream.of(clazz.getMethods())
                 .filter(m -> Modifier.isStatic(m.getModifiers()))
                 .filter(m -> m.getParameterCount() == 0)
                 .filter(m -> String.class.isAssignableFrom(m.getReturnType()))
                 .findAny()
                 .map(m -> invoke(null, String.class, m))
                 .orElse(null);
  }

  static Object invoke(Object o, Method method, Object... parameters) {
    try {
      return method.invoke(o, parameters);
    } catch (IllegalAccessException | InvocationTargetException e) {
      return null;
    }
  }

  static <T> T invoke(Object o, Class<T> clazz, Method method, Object... parameters) {
    return Optional.ofNullable(method)
                   .map(m -> invoke(o, m, parameters))
                   .filter(clazz::isInstance)
                   .map(clazz::cast)
                   .orElse(null);
  }

  public static CellChange from(Command command) {
    if (command == null) {
      return null;
    }
    Class<?> changeClass = findInFirstCome(CHANGE_MAP.keySet(), n -> command.getName().equals(n),
                                           n -> command.getName().equalsIgnoreCase(n))
        .map(CHANGE_MAP::get).orElse(null);

    if (changeClass != null) {
      return invoke(null, CellChange.class,
                    Stream.of(changeClass.getMethods())
                          .filter(m -> Modifier.isStatic(m.getModifiers()))
                          .filter(m -> m.getParameterCount() == 1)
                          .filter(m -> command.getClass().isAssignableFrom(m.getParameterTypes()[0]))
                          .filter(m -> CellChange.class.isAssignableFrom(m.getReturnType()))
                          .findAny()
                          .orElse(null), command);
    }

    return null;
  }

  static <T> T getLast(List<T> strings) {
    if (isEmpty(strings)) {
      return null;
    }
    return strings.get(strings.size() - 1);
  }

}
