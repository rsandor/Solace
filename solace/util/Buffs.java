package solace.util;

import java.util.*;
import java.io.*;
import org.json.*;
import solace.game.Buff;

/**
 * Utility class for loading buff metadata and generating buffs by name.
 * @author Ryan Sandor Richards
 */
public class Buffs {
  /**
   * Template for a buff that is loaded from a data file. This is not a buff as
   * used by the game engine, rather a set of metadata about a buff.
   * @author Ryan Sandor Richards
   */
  private static class BuffTemplate {
    String name;
    String description;
    boolean debuff;
    int defaultDuration;

    /**
     * Creates a new buff template with the given parameters.
     * @param n Name of the buff.
     * @param d Description of the buff.
     * @param db Whether or not it is a debuff.
     * @param du The default duration for the buff.
     */
    public BuffTemplate(String n, String d, boolean db, int du) {
      name = n;
      description = d;
      debuff = db;
      defaultDuration = du;
    }

    /**
     * @return The name of the buff.
     */
    public String getName() { return name; }

    /**
     * @return The description for the buff.
     */
    public String getDescription() { return description; }

    /**
     * @return True if this is a debuff, false otherwise.
     */
    public boolean isDebuff() { return debuff; }

    /**
     * @return The defaultDuration for the buff.
     */
    public int getDefaultDuration() { return defaultDuration; }
  }

  /**
   * Path to the races data directory.
   */
  static final String PATH = "data/buffs/";

  /**
   * Default duration, in seconds, for buffs which did not define said duration.
   */
  static final int DEFAULT_DURATION = 30;

  static Hashtable<String, BuffTemplate> templates;

  /**
   * Loads all races for the game.
   */
  public static void initialize() {
    File dir = new File(PATH);
    String[] names = dir.list();
    templates = new Hashtable<String, BuffTemplate>();

    Log.info("Loading buffs");

    for (String name : names) {
      try {
        Log.trace("Loading buff " + name);
        String path = PATH + name;
        StringBuffer contents = new StringBuffer("");
        BufferedReader in = new BufferedReader(new FileReader(path));
        String line = in.readLine();
        while (line != null) {
          contents.append(line + "\n");
          line = in.readLine();
        }

        JSONObject object = new JSONObject(contents.toString());
        BuffTemplate tpl = new BuffTemplate(
          object.getString("name"),
          object.getString("description"),
          object.getBoolean("debuff"),
          object.getInt("duration")
        );
        templates.put(tpl.getName(), tpl);
      } catch (JSONException je) {
        Log.error(String.format(
          "Malformed json in buff %s: %s", je.getMessage()
        ));
      } catch (IOException ioe) {
        Log.error("Unable to load buff: " + name);
        ioe.printStackTrace();
      }
    }
  }

  /**
   * Determines if there is a buff with the given name.
   * @param name Name of the buff.
   * @return True if a buff with the given name exists, false otherwise.
   */
  public static boolean has(String name) {
    return templates.containsKey(name);
  }

  /**
   * Creates an instance of the buff with the given name.
   * @param name Name of the buff to instantiate.
   * @return The newly created buff.
   */
  public static Buff create(String name) {
    int duration = has(name) ?
      templates.get(name).getDefaultDuration() :
      DEFAULT_DURATION;
    return new Buff(name, duration);
  }

  /**
   * Creates an instance of a buff with the given name and the given duration.
   * @param name Name of the buff to instantiate.
   * @param duration Duration for which the buff shall last.
   * @return The newly created buff.
   */
  public static Buff create(String name, int duration) {
    return new Buff(name, duration);
  }

  /**
   * Determines the description for the buff with the given name.
   * @param name Name of the buff.
   * @return The description for the buff.
   */
  public static String getDescription(String name) {
    return has(name) ? templates.get(name).getDescription() : "";
  }

  /**
   * Determines if the buff with the given name is a debuff.
   * @param name Name of the buff.
   * @return True if it is a debuff, false othwerwise.
   */
  public static boolean isDebuff(String name) {
    return has(name) ? templates.get(name).isDebuff() : false;
  }
}
