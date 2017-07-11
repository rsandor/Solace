package solace.io;

/**
 * Thrown when a skill could not be found for a given id.
 * @author Ryan Sandor Richards
 */
public class SkillNotFoundException extends Exception {
  public SkillNotFoundException(String msg) {
    super(msg);
  }
}
