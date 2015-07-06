package solace.game;

import solace.cmd.play.Move;
import solace.util.*;
import solace.game.*;
import java.util.*;
import solace.util.EventEmitter;
import solace.util.EventListener;

/**
 * Represents a mobile in the game world.
 * @author Ryan Sandor Richards
 */
public class Mobile extends Template implements Movable
{
  public enum State { STATIONARY, WANDERING }

  int level;
  int power;
  int hp;

  boolean isPlaced = false;
  State state = State.STATIONARY;
  Clock.Event wanderEvent;
  Room room;
  EventEmitter events;

  /**
   * Creates a new mobile.
   */
  public Mobile() {
    super();
    wanderEvent = null;
    events = new EventEmitter();
  }

  /**
   * @see solace.game.Movable
   */
  public String getName() { return get("description.name"); }

  /**
   * @see solace.game.Movable
   */
  public String getDescription() { return get("description"); }

  /**
   * @see solace.game.Movable
   */
  public void sendMessage(String msg) {
    events.trigger("message", new Object[] { msg });
  }

  /**
   * @see solace.game.Movable
   */
  public Room getRoom() { return room; }

  /**
   * @see solace.game.Movable
   */
  public void setRoom(Room r) { room = r; }

  /**
   * @return The level of the mobile.
   */
  public int getLevel() { return level; }

  /**
   * Sets the state of the mobile.
   * @param s State to set.
   */
  public void setState(State s) {
    state = s;

    if (state == State.STATIONARY) {
      if (wanderEvent != null) {
        wanderEvent.cancel();
        wanderEvent = null;
      }
    }
    else if (state == State.WANDERING) {
      // TODO Need to redo all this...
      //
      // if (wanderEvent == null) {
      //   wanderEvent = Clock.getInstance().interval(
      //     "mobile.wander",
      //     15,
      //     new Runnable() {
      //       public void run() {
      //         // Get a random exit and move there
      //         Random rand = new Random();
      //
      //         // 50-50 shot of just staying put
      //         if (rand.nextInt(2) == 0) {
      //           return;
      //         }
      //
      //         // Move to a random exit
      //         Room origin = character.getRoom();
      //         List<Exit> exits = origin.getExits();
      //         Exit exit = exits.get(rand.nextInt(exits.size()));
      //         String direction = exit.getNames().get(0);
      //         Room destination = area.getRoom(exit.getToId());
      //         if ( move.run(null, new String[] { direction }) ) {
      //           swapRooms(origin, destination);
      //         }
      //       }
      //     }
      //   );
      // }
    }
  }

  /**
   * Moves a mobile from one room to another.
   * @param origin Room to remove the mobile from.
   * @param destination Room to put the mobile in.
   */
  protected void swapRooms(Room origin, Room destination) {
    origin.getMobiles().remove(this);
    destination.getMobiles().add(this);
  }

  /**
   * @return The state of the mobile.
   */
  public State getState() {
    return state;
  }

  /**
   * Sets the state of the mobile based on its string representation.
   * @param name Name of the state to set for the mobile.
   */
  public void setState(String name) {
    if (name.startsWith("stationary")) {
      setState(State.STATIONARY);
    }
    else if (name.startsWith("wandering")) {
      setState(State.WANDERING);
    }
  }

  /**
   * Places the mobile into the game world.
   */
  public void place(Room room) {
    if (isPlaced) { return; }
    isPlaced = true;

    String state = get("state");
    setState(get("state"));

    String spawn = get("description.spawn");
    if (spawn == null) {
      spawn = get("description.name") + " enters.";
    }
    room.sendMessage(spawn);
    setRoom(room);

    // Set the level of the mob
    level = 1;
    String levelString = get("level");
    if (levelString != null) {
      try {
        level = Integer.parseInt(levelString);
      }
      catch (NumberFormatException nfe) {
        Log.error(String.format(
          "Invalid level for mobile %s: %s", id, levelString
        ));
      }
    }

    // TODO Add loot information to mobiles

    // Generate stats based on level and power
    power = 25;
    String powerLevel = get("power");
    try {
      if (powerLevel != null) {
        power = Integer.parseInt(powerLevel);
      }
    }
    catch (NumberFormatException nfe) {
      Log.error(String.format(
        "Invalid power for mobile %s: %s", id, levelString
      ));
    }

    // TODO Need to generate other statistics for the mobile

    room.getMobiles().add(this);
    room.getCharacters().add(this);
  }

  /**
   * Cancels the wander event on this mobile if applicable.
   */
  public void cancelWanderEvent() {
    if (wanderEvent != null) {
      wanderEvent.cancel();
    }
  }

  /**
   * Removes the mobile from the game world.
   */
  public void pluck() {
    if (!isPlaced) { return; }
    if (wanderEvent != null) {
      wanderEvent.cancel();
    }
    Room room = getRoom();
    room.getCharacters().remove(this);
    room.getMobiles().remove(this);
    setRoom(null);
    isPlaced = false;
  }

  /**
   * Adds an event listener to the mobile.
   * @param event Name of the event.
   * @param listener Listener for the event.
   */
  public void addEventListener(String event, EventListener listener) {
    events.addListener(event, listener);
  }

  /**
   * Removes an event listener from the mobile.
   * @param event Name of the event.
   * @param listener Listener for the event.
   */
  public void removeEventListener(String event, EventListener listener) {
    events.removeListener(event, listener);
  }
}
