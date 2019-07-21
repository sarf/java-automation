package sarf.automation.poi;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class ApplicationCommands {

  @NonNull
  private final String input;

  private final String output;

  @NonNull
  private final List<Command> commands;

  @Data
  public static class Command {
    private final String name;
    @NonNull
    private final List<String> strings;
  }

}
