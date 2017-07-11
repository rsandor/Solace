package solace.io;

/**
 * Exception thrown when an emote file is incorrectly formatted.
 * @author Ryan Sandor Richards
 */
public class InvalidEmoteException extends Exception {
  public InvalidEmoteException(String msg) {
    super(msg);
  }
}
