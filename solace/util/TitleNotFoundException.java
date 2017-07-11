package solace.util;

/**
 * Exception thrown when a markdown document title could not be found.
 */
public class TitleNotFoundException extends Exception {
  public TitleNotFoundException() {
    super("Title for markdown document could not be found.");
  }
}
