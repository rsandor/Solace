package solace.util;

/**
 * Exception thrown when an emote with the given prefix could not be found by
 * the Emotes class.
 * @author Ryan Sandor Richards
 */
public class EmoteNotFoundException extends Exception {
  public EmoteNotFoundException(String msg) {
    super(msg);
  }
}
