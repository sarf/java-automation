package sarf.automation.poi.util;

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
    @NonNull private final Matcher matcher;

    private boolean found;

    public EasyMatcher find() {
      found = matcher.find();
      return this;
    }

    public String toGroup(int group) {
      try {
        return matcher.group(group);
      } catch (IllegalStateException|IndexOutOfBoundsException e) {
        return null;
      }
    }

  }
}
