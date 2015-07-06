package solace.game;

/**
 * Behavior set for objects representing people or monsters that can inhabit
 * and move between rooms.
 * @author Ryan Sandor Richards
 */
public interface Movable {
  /**
   * @return A name by which the object is referenced.
   */
  public String getName();

  /**
   * @return A string describing the object.
   */
  public String getDescription();

  /**
   * Sends the movable a message coming from the room they inhabit. Examples
   * include player communication, another movable entering the room, etc.
   * @param s Message to sent the movable.
   */
  public void sendMessage(String s);

  /**
   * @return The room the movable currently occupies.
   */
  public Room getRoom();

  /**
   * Sets the room for the movable.
   * @param r Room to set.
   */
  public void setRoom(Room r);
}
