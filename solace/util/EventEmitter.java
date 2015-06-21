package solace.util;

import java.util.*;

/**
 * Base eventing class. Can be used to listen for and trigger named events.
 * @author Ryan Sandor Richards
 */
public class EventEmitter {
  Hashtable<String, List<EventListener>> listeners;

  /**
   * Creates a new event emitter.
   */
  public EventEmitter() {
    listeners = new Hashtable<String, List<EventListener>>();
  }

  /**
   * Determines the listener list for a given even name.
   * @param event Name of the event.
   * @return A list of listeners for the given event.
   */
  private List<EventListener> getList(String event) {
    if (!listeners.containsKey(event)) {
      List<EventListener> listenerList = Collections.synchronizedList(
        new LinkedList<EventListener>()
      );
      listeners.put(event, listenerList);
      return listenerList;
    }
    return listeners.get(event);
  }

  /**
   * Adds a listener for the given event name.
   * @param event Name of the event.
   * @param listener Listener to register for the event.
   */
  public void addListener(String event, EventListener listener) {
    List<EventListener> listeners = getList(event);
    synchronized(listeners) {
      listeners.add(listener);
    }
  }

  /**
   * Removes a listener for the given event name.
   * @param event Name of the event.
   * @param listener Listener to register for the event.
   */
  public void removeListener(String event, EventListener listener) {
    List<EventListener> listeners = getList(event);
    synchronized(listeners) {
      listeners.remove(listener);
    }
  }

  /**
   * Triggers an event with a list of argument.
   */
  public void trigger(String event, Object[] arguments) {
    List<EventListener> listeners = getList(event);
    synchronized(listeners) {
      for (EventListener l : listeners) {
        l.run(arguments);
      }
    }
  }
}
