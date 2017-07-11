package solace.io.xml;

/**
 * Exception thrown in response to a parse error while parsing an area XML file.
 * @author Ryan Sandor Richards
 */
public class AreaParseException extends Exception {
  /**
   * Creates a new exception.
   * @param fileName Name of the file being parsed.
   * @param msg Message detailing the exact nature of the exception.
   */
  public AreaParseException(String fileName, String msg) {
    super("Parse error in '" + fileName + "': " + msg);
  }
}
