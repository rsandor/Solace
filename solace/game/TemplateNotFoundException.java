package solace.game;

/**
 * Exception that is thrown when a template could not be found by the
 * TemplateFactory.
 * @author Ryan Sandor Richards
 */
public class TemplateNotFoundException extends Exception {
  /**
   * Creates a new TemplateNotFoundException.
   * @param id Id of the template that was missing.
   */
  public TemplateNotFoundException(String id) {
    super("Template with id '" + id + "' could not be found.");
  }
}
