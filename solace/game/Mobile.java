package solace.game;
import solace.util.Log;

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

  /**
   * Creates a new mobile.
   */
  public Mobile() {
    super();
    character = new solace.game.Character("");
  }

  /**
   * Places the mobile into the game world.
   */
  public void place(Room room) {
    if (isPlaced) { return; }
    isPlaced = true;

    Log.info("Placing " + get("description.name"));

    character.setName(get("description.name"));
    character.setDescription(get("description"));

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
