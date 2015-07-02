package solace.game;

import org.json.*;
import java.util.*;

/**
 * Represents a skill in the game world.
 * @author Ryan Sandor Richards.
 */
public class Skill {
  /**
   * Tuple containing the name of a proficiency, passive, or cooldown and the
   * skill level at which it is acquired.
   * @author Ryan Sandor Richards
   */
  private static class LevelPair {
    int level;
    String name;

    /**
     * Creates a new level pair.
     * @param l Level for the pair.
     * @param n Name for the pair.
     */
    public LevelPair(int l, String n) {
      level = l;
      name = n;
    }

    /**
     * @return The level for the pair.
     */
    public int getLevel() { return level; }

    /**
     * @return The name for the pair.
     */
    public String getName() { return name; }
  }

  String id;
  String name;
  int level;
  Collection<LevelPair> proficiencies;
  Collection<LevelPair> passives;
  Collection<LevelPair> cooldowns;

  /**
   * Creates a new empty skill.
   * @param i Id of the skill.
   * @param n Name of the skill.
   */
  public Skill(String i, String n) {
    id = i;
    name = n;
    level = 1;
    proficiencies = new ArrayList<LevelPair>();
    passives = new ArrayList<LevelPair>();
    cooldowns = new ArrayList<LevelPair>();
  }

  /**
   * @return A clone of the skill.
   */
  public Skill clone() {
    Skill s = new Skill(id, name);
    s.proficiencies = proficiencies;
    s.passives = passives;
    s.cooldowns = cooldowns;
    return s;
  }

  /**
   * Adds a weapon proficiency to the skill.
   * @param prof proficiency to add.
   */
  protected void addProficiency(LevelPair prof) {
    proficiencies.add(prof);
  }

  /**
   * Adds a passive enhancement to the skill.
   * @param passive Passive to add.
   */
  protected void addPassive(LevelPair passive) {
    passives.add(passive);
  }

  /**
   * Adds a cooldown to the skill.
   * @param cooldown Cooldown to add to the skill.
   */
  protected void addCooldown(LevelPair cooldown) {
    cooldowns.add(cooldown);
  }

  /**
   * @return the skill's name.
   */
  public String getName() { return name; }

  /**
   * @return the skill's id.
   */
  public String getId() { return id; }

  /**
   * @return The level of the skill.
   */
  public int getLevel() { return level; }

  /**
   * Sets the level of the skill.
   * @param l Level to set.
   */
  public void setLevel(int l) { level = l; }

  /**
   * Constructs a collection of names for the given collection of level pairs
   * that have a level less than or equal to the current level of the skill.
   * @param c Collection to search.
   * @return The resulting collection of names.
   */
  protected Collection<String> getNamesForLevel(Collection<LevelPair> c) {
    LinkedList<String> result = new LinkedList<String>();
    for (LevelPair pair : c) {
      if (level >= pair.getLevel()) {
        result.add(pair.getName());
      }
    }
    return result;
  }

  /**
   * @return a list of passives granted by the skill at its current level.
   */
  public Collection<String> getPassives() {
    return getNamesForLevel(passives);
  }

  /**
   * Determine whether the skill grants the given passive at its current level.
   * @param name Name of the passive enhancement.
   * @return True if it grants the passive, false otherwise.
   */
  public boolean grantsPassive(String name) {
    return getPassives().contains(name);
  }

  /**
   * @return A list of the cooldowns granted by the skill at its current level.
   */
  public Collection<String> getCooldowns() {
    return getNamesForLevel(cooldowns);
  }

  /**
   * Determine whether the skill grants the given cooldown at its current level.
   * @param name Name of the cooldown action.
   * @return True if it grants the cooldown, false otherwise.
   */
  public boolean grantsCooldown(String name) {
    return getCooldowns().contains(name);
  }

  /**
   * @return List of the proficiencies granted by the skill at its current level.
   */
  public Collection<String> getProficiencies() {
    return getNamesForLevel(proficiencies);
  }

  /**
   * Determine whether the skill grants the given proficiency at its current
   * level.
   * @param name Name of the proficiency.
   * @return True if it grants the proficiency, false otherwise.
   */
  public boolean grantsProficiency(String name) {
    return getProficiencies().contains(name);
  }

  /**
   * Parses skill JSON to create a new Skill instance.
   * @param json JSON to parse.
   * @return The parsed skill.
   */
  public static Skill parseJSON(String json)
    throws JSONException
  {
    JSONObject object = new JSONObject(json);
    String id = object.getString("id");
    String name = object.getString("name");
    Skill skill = new Skill(id, name);
    int i, j;

    JSONArray profs = object.getJSONArray("proficiencies");
    for (i = 0; i < profs.length(); i++) {
      JSONObject profObj = profs.getJSONObject(i);
      int profLevel = profObj.getInt("level");
      JSONArray profTypes = profObj.getJSONArray("types");
      for (j = 0; j < profTypes.length(); j++) {
        String profType = profTypes.getString(j);
        skill.addProficiency(new LevelPair(
          profLevel, profType
        ));
      }
    }

    JSONArray pass = object.getJSONArray("passives");
    for (i = 0; i < pass.length(); i++) {
      JSONObject passiveObj = pass.getJSONObject(i);
      skill.addPassive(new LevelPair(
        passiveObj.getInt("level"),
        passiveObj.getString("name")
      ));
    }

    JSONArray cool = object.getJSONArray("cooldowns");
    for (i = 0; i < cool.length(); i++) {
      JSONObject coolObj = cool.getJSONObject(i);
      skill.addCooldown(new LevelPair(
        coolObj.getInt("level"),
        coolObj.getString("name")
      ));
    }

    return skill;
  }
}
