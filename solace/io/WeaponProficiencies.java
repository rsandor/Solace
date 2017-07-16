package solace.io;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import solace.game.WeaponProficiency;
import solace.util.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Utility class for loading and referencing weapon proficiencies by name.
 * @author Ryan Sandor Richards
 */
public class WeaponProficiencies {
  public static final WeaponProficiencies instance = new WeaponProficiencies();

  private final Hashtable<String, WeaponProficiency> proficiencies = new Hashtable<>();

  /**
   * @return The singleton instance of the weapons utility.
   */
  public static WeaponProficiencies getInstance() { return instance; }

  /**
   * Initializes the skills helper by loading all the skills provided in the
   * game data directory.
   */
  public void reload() throws IOException {
    Log.info("Loading weapon proficiencies");
    proficiencies.clear();
    GameFiles.findWeaponProficiencyFiles().forEach(path -> {
      String name = path.getFileName().toString();
      try {
        String json = new String(Files.readAllBytes(path));
        JSONArray all = new JSONArray(json);
        for (int i = 0; i < all.length(); i++) {
          JSONObject profJson = all.getJSONObject(i);
          WeaponProficiency proficiency = new WeaponProficiency(
            profJson.getString("name"),
            profJson.getString("type"),
            profJson.getString("style"),
            profJson.getString("skill"),
            profJson.getInt("hands"),
            Arrays.asList(profJson.getString("damage").split("\\s*,\\s*"))
          );
          if (has(proficiency.getName())) {
            Log.warn(String.format("Duplicate weapon proficiency name '%s' found in '%s', skipping.",
              proficiency.getName(), name));
            continue;
          }
          Log.trace(String.format("Adding weapon proficiency '%s'", proficiency.getName()));
          proficiencies.put(proficiency.getName(), proficiency);
        }
      } catch (JSONException je) {
        Log.error(String.format("Malformed json in weapon proficiency %s: %s", name, je.getMessage()));
      } catch (IOException ioe) {
        Log.error(String.format("Unable to load weapon proficiency: %s", name));
        ioe.printStackTrace();
      }
    });
  }

  /**
   * Determines if there is a weapon proficiency with the given name.
   * @param name Name of the weapon proficiency.
   * @return True if a weapon proficiency with the given name exists, false otherwise.
   */
  public boolean has(String name) {
    return proficiencies.containsKey(name);
  }

  /**
   * Gets the weapon proficiency with the given name.
   * @param name Name of the weapon proficiency.
   * @return The weapon proficiency with the given name.
   */
  public WeaponProficiency get(String name) {
    return proficiencies.get(name);
  }
}
