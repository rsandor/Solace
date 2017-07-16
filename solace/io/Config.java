package solace.io;

import solace.util.Log;
import solace.io.xml.GameParser;
import java.io.*;
import java.nio.file.Path;
import java.util.Hashtable;

/**
 * Helper class for loading and accessing configuration files.
 * @author Ryan Sandor Richards.
 */
public class Config {
  private static Hashtable<String, Configuration> configurations = new Hashtable<>();

  /**
   * Loads a configuration file.
   * @param path Path to the configuration file.
   */
  private static void loadConfig(Path path) {
    String filename = String.valueOf(path);
    try {
      Configuration c = GameParser.parseConfiguration(filename);
      configurations.put(c.getName(), c);
      Log.trace("Configuration \"" + filename + "\" loaded.");
    } catch (IOException ioe) {
      Log.error("Unable to load configuration file: " + filename);
    }
  }

  /**
   * Loads all configuration files.
   */
  public static void load() {
    try {
      Log.info("Loading configurations");
      GameFiles.findConfigurations().forEach(Config::loadConfig);
    } catch (IOException e) {
      Log.fatal("Failed to read configurations from game directory, exiting.");
      System.exit(0);
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

    StringBuilder keyBuffer = new StringBuilder();
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
