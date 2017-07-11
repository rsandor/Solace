package solace.io;

/**
 * Thrown when a required annotation is missing from a markdown file.
 * @author Ryan Sandor Richards.
 */
public class RequiredAnnotationException extends Exception {
  /**
   * Creates a new RequiredAnnotationException for the given annotation name.
   * @param annotationName Name of the missing annotation.
   */
  public RequiredAnnotationException(String annotationName) {
    super(String.format("Missing '%s' annotation.", annotationName));
  }
}
