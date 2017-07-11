package solace.util;

/**
 * Thrown when a help page with the given name could not be found.
 * @author Ryan Sandor Richards
 */
public class HelpPageNotFoundException extends Exception {
  public HelpPageNotFoundException(String name) {
    super(String.format("Help page with name '%s' not found.", name));
  }
}
