package solace.game;

/**
 * Exception that is thrown when a problem involving game currency (gold, etc.)
 * occurs.
 * @author Ryan Sandor Richards
 */
public class CurrencyException extends Exception {
  /**
   * Creates a new CurrencyException.
   * @param msg Message for the exception.
   */
  public CurrencyException(String msg) {
    super(msg);
  }
}
