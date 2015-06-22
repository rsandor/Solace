package solace.game;

import solace.cmd.play.Move;
import solace.util.*;
import solace.game.*;
import java.util.*;

/**
 * Represents a mobile in the game world.
 * @author Ryan Sandor Richards
 */
public class Mobile extends Template {
  public enum State {
    STATIONARY,
    WANDERING
  }

  private solace.game.Character character;
  private boolean isPlaced = false;
  private State state = State.STATIONARY;

  // Allows the mobile to be, well, mobile...
  private Move move;
  private Clock.Event wanderEvent;

  /**
   * Creates a new mobile.
   */
  public Mobile() {
    super();
    character = new solace.game.Character("");
    move = new Move(character);
    wanderEvent = null;
  }

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
      if (wanderEvent == null) {
        wanderEvent = Clock.getInstance().interval(15, new Runnable() {
          public void run() {
            // Get a random exit and move there
            Random rand = new Random();

            // 50-50 shot of just staying put
            if (rand.nextInt(2) == 0) {
              return;
            }

            // Move to a random exit
            List<Exit> exits = character.getRoom().getExits();
            Exit exit = exits.get(rand.nextInt(exits.size()));
            String direction = exit.getNames().get(0);
            move.run(null, new String[] { direction });
          }
        });
      }
    }
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

    character.setName(get("description.name"));
    character.setDescription(get("description"));

    String state = get("state");
    setState(get("state"));

    String spawn = get("description.spawn");
    if (spawn == null) {
      spawn = get("description.name") + " enters.";
    }
    room.sendMessage(spawn);
    character.setRoom(room);
    room.getCharacters().add(character);
  }

  /**
   * Removes the mobile from the game world.
   */
  public void pluck() {
    if (!isPlaced) { return; }
    character.getRoom().getCharacters().remove(character);
    character.setRoom(null);
    isPlaced = false;
  }
}
