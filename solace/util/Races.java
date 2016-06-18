package solace.util;

import java.util.*;
import java.io.*;
import org.json.*;
import solace.game.Race;

/**
 * Utility class for loading and referencing races by name.
 * @author Ryan Sandor Richards
 */
public class Races {
  /**
   * Path to the races data directory.
   */
  protected static final String PATH = "data/races/";

  static Hashtable<String, Race> races;

  /**
   * Loads all races for the game.
   */
  public static void initialize() {
    File dir = new File(PATH);
    String[] names = dir.list();
    races = new Hashtable<String, Race>();

    Log.info("Loading races");

    for (String name : names) {
      try {
        Log.trace("Loading race " + name);
        String path = PATH + name;
        StringBuffer contents = new StringBuffer("");
        BufferedReader in = new BufferedReader(new FileReader(path));
        String line = in.readLine();
        while (line != null) {
          contents.append(line + "\n");
          line = in.readLine();
        }
        Race race = Race.parseJSON(contents.toString());
        races.put(race.getName(), race);
      } catch (JSONException je) {
        Log.error(String.format(
          "Malformed json in race %s: %s", je.getMessage()
        ));
      } catch (IOException ioe) {
        Log.error("Unable to load race: " + name);
        ioe.printStackTrace();
      }
    }
  }

  /**
   * Determiens if there is a race with the given name.
   * @param name Name of the race.
   * @return True if a race with the given name exists, false otherwise.
   */
  public static boolean has(String name) {
    return races.containsKey(name);
  }

  /**
   * Gets the race with the given name.
   * @param name Name of the race.
   * @return The race with the given name.
   */
  public static Race get(String name) {
    if (!has(name)) return null;
    return races.get(name);
  }
}
