package solace.game;

/**
 * Exception thrown when an item was expected to be a piece of equipment but
 * was not.
 * @author Ryan Sandor Richards
 */
public class NotEquipmentException extends Exception {
  public NotEquipmentException(String msg) {
    super(msg);
  }
}
