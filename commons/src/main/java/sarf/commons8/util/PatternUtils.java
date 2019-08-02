package sarf.commons8.util;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.function.Consumer;
import java.util.regex.Matcher;

import static sarf.commons8.util.FunctionConversionUtil.reuse;

public interface PatternUtils {

  @SuppressWarnings("ResultOfMethodCallIgnored")
  static Matcher find(Matcher m) {
    return reuse(m, Matcher::find);
  }

  @SuppressWarnings("WeakerAccess")
  @EqualsAndHashCode
  @ToString
  class EasyMatcher {

    @NonNull
    private final Matcher matcher;
    private boolean found;

    public EasyMatcher(@NonNull Matcher matcher) {
      this.matcher = matcher;
    }

    public static EasyMatcher of(@NonNull Matcher matcher) {
      return new EasyMatcher(matcher);
    }

    public EasyMatcher match() {
      found = matcher.matches();
      return this;
    }

    public EasyMatcher find() {
      found = matcher.find();
      return this;
    }

    public EasyMatcher consumeFound(Consumer<Boolean> consumer) {
      consumer.accept(found);
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
