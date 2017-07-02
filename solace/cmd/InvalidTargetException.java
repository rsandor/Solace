package solace.cmd;

/**
 * Exception to be thrown when a target for an attack or command is invalid.
 * @author Ryan Sandor Richards
 */
public class InvalidTargetException extends GameException {
  public InvalidTargetException(String msg) {
    super(msg);
  }
}
