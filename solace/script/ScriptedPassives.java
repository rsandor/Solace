package solace.script;

import solace.game.Passive;
import solace.util.Log;

import java.util.Hashtable;

/**
 * Holds scripted passives that were created during execution of game scripts.
 * @author Ryan Sandor Richards
 */
public class ScriptedPassives {
  private static final Hashtable<String, Passive> passives = new Hashtable<>();

  /**
   * Clears all scripted passives.
   */
  public static void clear() {
    passives.clear();
  }

  /**
   * Adds a scripted passive.
   * @param p The passive to add.
   */
  public static void add(Passive p) {
    if (passives.keySet().contains(p.getName())) {
      Log.warn(String.format(
        "Encountered passive with duplicate name '%s', skipping.", p.getName()));
      return;
    }
    Log.debug(String.format("Adding passive '%s'", p.getName()));
    passives.put(p.getName(), p);
  }

  /**
   * Determines whether or not a passive of the given name exists.
   * @param name Name of the passive to find.
   * @return True if the passive exists, false otherwise.
   */
  public static boolean has(String name) {
    return passives.keySet().contains(name);
  }

  /**
   * Finds the scripted passive with the given name.
   * @param name Name of the passive to find.
   * @return The passive with the given name.
   * @throws PassiveNotFoundException If a passive with the given name does not exist.
   */
  public static Passive get(String name) throws PassiveNotFoundException {
    if (!has(name)) {
      throw new PassiveNotFoundException(name);
    }
    return passives.get(name);
  }
}
