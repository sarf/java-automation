package sarf.automation.events.imap.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

class ListenerHandler<T> {

  private final Collection<T> listeners;

  ListenerHandler() {
    this(ArrayList::new);
  }

  private ListenerHandler(Supplier<Collection<T>> supplier) {
    this.listeners = supplier.get();
  }

  T addListener(T listener) {
    if (!listeners.contains(listener) && listeners.add(listener)) {
      return listener;
    }
    return listeners.stream()
                    .filter(listener::equals)
                    .findAny()
                    .orElse(null);
  }

  T removeListener(T listener) {
    if (listeners.remove(listener)) {
      return listener;
    }
    return null;
  }

  Iterator<T> iterateListeners() {
    return new ArrayList<>(listeners).iterator();
  }
}
