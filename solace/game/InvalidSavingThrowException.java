package solace.game;

/**
 * Exception thrown when encountering an invalid saving throw name.
 * @author Ryan Sandor Richards
 */
public class InvalidSavingThrowException extends Exception {
  public InvalidSavingThrowException(String msg) {
    super(msg);
  }
}
