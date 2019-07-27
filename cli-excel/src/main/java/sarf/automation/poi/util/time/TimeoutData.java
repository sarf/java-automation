package sarf.automation.poi.util.time;

import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.NonNull;

@Data
public class TimeoutData {

  private final long amount;

  @NonNull
  private final TimeUnit unit;

  public TimeoutData(long amount, @NonNull TimeUnit unit) {
    if(amount < 0) throw new IllegalArgumentException("amount of timeout can not be less than zero");
    this.amount = amount;
    this.unit = unit;
  }
}
