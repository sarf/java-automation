package sarf.automation.poi.util;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

public interface PatternUtils {

  static Matcher find(Matcher m) {
    return FunctionUtils.consumeRecycle(m, Matcher::find);
  }

  @Data(staticConstructor = "of")
  @Getter(AccessLevel.PROTECTED)
  class EasyMatcher {

    @NonNull
    private final Matcher matcher;

    private boolean found;

    public EasyMatcher match() {
      found = matcher.matches();
      return this;
    }

    public EasyMatcher find() {
      found = matcher.find();
      return this;
    }

    public EasyMatcher consumeGroup(int group, Consumer<String> consumer) {
      consumer.accept(toGroup(group));
      return this;
    }

    public String toGroup(int group) {
      try {
        if (group >= 0 && group <= matcher.groupCount()) {
          return matcher.group(group);
        }
      } catch (IllegalStateException | IndexOutOfBoundsException e) {
        // Ignore as it should not happen.
      }
      return null;
    }

  }
}
