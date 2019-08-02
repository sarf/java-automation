package sarf.commons8.extensions;

import lombok.*;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("WeakerAccess")
@Data
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
public class WriteOnce<T> {

  private AtomicBoolean set = new AtomicBoolean(false);
  private T entity;

  /**
   * Reads the entity if it has been written to, otherwise throws an {@link IllegalStateException}.
   *
   * @return the written entity if it has been written to
   * @throws IllegalStateException if it has not been written to
   */
  public T readIfWritten() {
    if (set.get()) {
      return entity;
    }
    throw new IllegalStateException("can not read when not written");
  }

  /**
   * True if the object has had its entity written to, otherwise false.
   *
   * @return true if the object has had its entity written to, otherwise false.
   */
  public boolean written() {
    return set.get();
  }

  /**
   * This method retrieves the entity state, no matter if it has been written or not. Thus, getting null as a return
   * value here can mean either a) that the value is set to null or b) that the value has yet to be set.
   *
   * @return the entity written (may be null) or null if nothing has been written
   */
  public T get() {
    return entity;
  }

  /**
   * If the entity has not been written to, do so using the entity provided and ensure that it can not be written to
   * again. If the entity has been written to, throw an {@link IllegalStateException}.
   *
   * @param entity the entity that is intended to be written
   */
  @Synchronized("set")
  public void write(T entity) {
    if (set.getAndSet(true)) {
      throw new IllegalStateException("can only be written once");
    }
    this.entity = entity;
  }

  /**
   * If the entity contained in this class has yet to be written, true is returned and the entity is written to it. If
   * the entity has already been written to, false is returned.
   *
   * @param entity the entity that is wished for
   * @return true if the entity was written to the object, otherwise false
   */
  @SuppressWarnings("UnusedReturnValue")
  @Synchronized("set")
  public boolean writeIfUnwritten(T entity) {
    if (set.getAndSet(true)) {
      return false;
    }
    this.entity = entity;
    return true;
  }

}
