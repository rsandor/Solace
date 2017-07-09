package solace.util;
import java.util.Hashtable;

/**
 * Object representation of a custom configuration file.
 * @author Ryan Sandor Richards
 */
public class Configuration extends Hashtable<String, String> {
  private String name;

  /**
   * @param name Name of the configuration.
   */
  public Configuration(String name) {
    super();
    this.name = name;
  }

  /**
   * @return The configuration's name.
   */
  public String getName() { return name; }
}
