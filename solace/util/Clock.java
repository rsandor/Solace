package solace.util;

import java.util.*;
import java.util.concurrent.*;

import solace.util.*;

/**
 * Represnts the game world's clock.
 */
public class Clock
  implements Runnable
{
  /**
   * An event that can be scheduled on the game clock.
   */
  public class Event {
    long delay;
    long initialDelay;
    Runnable action;
    boolean isInterval = false;

    /**
     * Creates a new game clock event with the given delay.
     * @param d Delay in game ticks.
     * @param a Action to perform.
     * @param i True if the action is a set interval, false otherwise.
     */
    public Event(long d, Runnable a, boolean i) {
      initialDelay = d;
      delay = d;
      action = a;
      isInterval = i;
    }

    /**
     * Advances the delay clock forward by one tick. If the duration has
     * elapsed, then this also executes the event action.
     * @return <code>true</code> if the action executed, <code>false</code>
     *   otherwise.
     */
    protected boolean tick() {
      delay--;

      if (delay < 0) {
        return false;
      }

      if (delay == 0) {
        action.run();
        if (isInterval) {
          delay = initialDelay;
          return false;
        }
        return true;
      }

      return false;
    }

    /**
     * Cancels the game event.
     */
    public void cancel() {
      isInterval = false;
      delay = -1;
    }
  }

  ScheduledExecutorService executor;
  ScheduledFuture tickFuture;
  List<Event> events;

  /**
   * Creates a new clock.
   */
  public Clock() {
    executor = Executors.newScheduledThreadPool(1);
    events = Collections.synchronizedList(new LinkedList<Event>());
  }

  /**
   * Starts the game world clock.
   */
  public void start() {
    // Don't start the clock if it is already running
    if (tickFuture != null) {
      return;
    }

    int tickMs = Integer.parseInt(Config.get("world.clock.tick"));
    Log.info("Starting game clock, with tick interval " + tickMs);
    tickFuture = executor.scheduleAtFixedRate(
      this,
      0,
      tickMs,
      TimeUnit.MILLISECONDS
    );
  }

  /**
   * Pauses the game world clock.
   */
  public void pause() {
    // Ignore multiple clock stops
    if (tickFuture == null) {
      return;
    }
    tickFuture.cancel(false);
    tickFuture = null;
  }

  /**
   * Stops the game clock entirely.
   */
  public void stop() {
    pause();
    executor.shutdownNow();
  }

  /**
   * Schedules an event on the clock.
   * @param delay Delay in ticks to wait before performing the action.
   * @param action Action to perform.
   */
  public void schedule(long delay, Runnable action) {
    events.add(new Event(delay, action, false));
  }

  /**
   * Sets an event to be repeated periodically for a set interval.
   * @param delay Length of the delay between each execution.
   * @param action Action to execute at the set interval.
   * @return Clock event that can be cancelled.
   */
  public Event interval(long delay, Runnable action) {
    Event event = new Event(delay, action, true);
    events.add(event);
    return event;
  }

  /**
   * Moves the game time clock forward one tick.
   */
  public void run() {
    synchronized(events) {
      Iterator<Event> iter = events.iterator();
      for (iter = events.iterator(); iter.hasNext(); ) {
        Event event = iter.next();
        if (event.tick()) {
          iter.remove();
        }
      }
    }
  }

  /**
   * The game wold has but one clock.
   */
  private static final Clock instance = new Clock();

  /**
   * Returns the game clock instance.
   */
  public static Clock getInstance() {
    return instance;
  }
}
