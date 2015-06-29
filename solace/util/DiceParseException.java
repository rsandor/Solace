package solace.util;

/**
 * Exception thrown when a dice roll is given an invalid pattern.
 * @author Ryan Sandor Richards
 */
public class DiceParseException extends Exception {
  public DiceParseException(String msg) {
    super(msg);
  }
}
