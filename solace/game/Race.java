package solace.game;

import org.json.*;
import java.util.*;

public class Race {
  String name;
  Collection<String> passives;
  Collection<String> cooldowns;

  /**
   * Creates a new race with the given name.
   * @param n Name of the race.
   */
  public Race(String n) {
    name = n;
    passives = new ArrayList<String>();
    cooldowns = new ArrayList<String>();
  }

  /**
   * @return The name of the race.
   */
  public String getName() {
    return name;
  }

  /**
   * Adds a passive ability to the race.
   * @param name Name of the passive to add.
   */
  public void addPassive(String name) {
    passives.add(name);
  }

  /**
   * @return An unmodifiable collection of passives granted by this race.
   */
  public Collection<String> getPassives() {
    return Collections.unmodifiableCollection(passives);
  }

  /**
   * Adds a cooldown skill to this race.
   * @param name Name of the cooldown skill to add.
   */
  public void addCooldown(String name) {
    cooldowns.add(name);
  }

  /**
   * @return An unmodifiable collection of cooldowns granted by this race.
   */
  public Collection<String> getCooldowns() {
    return Collections.unmodifiableCollection(cooldowns);
  }

  /**
   * Parses race JSON to create a new Race instance.
   * @param json JSON to parse.
   * @return The parsed race.
   */
  public static Race parseJSON(String json) throws JSONException {
    JSONObject object = new JSONObject(json);
    Race race = new Race(object.getString("name"));

    JSONArray pass = object.getJSONArray("passives");
    for (int i = 0; i < pass.length(); i++)
      race.addPassive(pass.getString(i));

    JSONArray cool = object.getJSONArray("cooldowns");
    for (int i = 0; i < cool.length(); i++)
      race.addCooldown(cool.getString(i));

    return race;
  }
}
