package solace.cmd;

/**
 * Base exception class for gameplay related errors.
 * @author Ryan Sandor Richards
 */
public class GameException extends Exception {
  public GameException(String msg) {
    super(msg);
  }
}