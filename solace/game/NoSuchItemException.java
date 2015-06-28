package solace.game;

/**
 * Exception thrown when an item assumed to be in a player's possession is not.
 * @author Ryan Sandor Richards
 */
public class NoSuchItemException extends Exception {
  public NoSuchItemException(String msg) {
    super(msg);
  }
}
