package solace.util;

import solace.xml.GameParser;
import java.io.*;
import java.util.Hashtable;

/**
 * Helper class for loading and accessing configuration files.
 * @author Ryan Sandor Richards.
 */
public class Config {
  /**
   * Holds parsed configurations.
   */
  protected static Hashtable<String, Configuration> configurations =
    new Hashtable<String, Configuration>();

  /**
   * Path to the configurations directory.
   */
  protected static final String configPath = "data/config/";

  /**
   * Loads a configuration file.
   * @param name Name of the file to load.
   */
  protected static void loadFile(String name) {
    try {
      Configuration c = GameParser.parseConfiguration(configPath + name);
      configurations.put(c.getName(), c);
      Log.info("Configuration \"" + name + "\" loaded.");
    }
    catch (IOException ioe) {
      Log.error("Unable to load configuration file: " + name);
    }
  }

  /**
   * Loads all configuration files.
   */
  public static void load() {
    File dir = new File(configPath);
    String[] names = dir.list();
    if (names != null) {
      for (String name : names) {
        if (name.equals("equipment.xml")) {
          continue;
        }
        loadFile(name);
      }
    }
    else {
      Log.info("No configurations to load.");
    }
  }

  /**
   * Retrieves a configuration value.
   * @param name Scoped name for the value.
   * @return The value for the name, or <code>null</code> if no such value could
   *   be found.
   */
  public static String get(String name) {
    String[] parts = name.split("\\.");
    String cfgName = parts[0];

    if (!configurations.containsKey(cfgName))
      return null;

    StringBuffer keyBuffer = new StringBuffer();
    for (int i = 1; i < parts.length; i++) {
      keyBuffer.append(parts[i]);
      if (i != parts.length - 1)
        keyBuffer.append(".");
    }

    String key = keyBuffer.toString();

    if (!configurations.get(cfgName).containsKey(key))
      return null;

    return configurations.get(cfgName).get(key);
  }
}
