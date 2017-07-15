package solace.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a weapon proficiency
 */
public class WeaponProficiency {
  /**
   * Represent the "simple" weapon proficiency type.
   */
  public static final String TYPE_SIMPLE = "simple";

  /**
   * Represents the "martial" weapon proficiency type.
   */
  public static final String TYPE_MARTIAL = "martial";

  private String name;
  private String type;
  private String skill;
  private int hands;
  private Collection<String> damageTypes = new ArrayList<>();

  /**
   * Creates a new weapon proficiency with the given parameters.
   * @param name Name for the proficiency.
   * @param type Type of proficiency.
   * @param skill Skill associated with the proficiency.
   * @param hands Number of hands needed to wield weapons that have the proficiency.
   * @param damTypes Damage types for weapons that have the proficiency.
   */
  public WeaponProficiency(String name, String type, String skill, int hands, Collection<String> damTypes) {
    setName(name);
    setType(type);
    setSkill(skill);
    setHands(hands);
    damageTypes = damTypes;
  }

  /**
   * @return The name of the weapon proficiency.
   */
  public String getName() { return name; }

  /**
   * Sets the name of the weapon proficiency.
   * @param name The name to set.
   */
  public void setName(String name) { this.name = name; }

  /**
   * @return The type of the weapon proficiency.
   */
  public String getType() { return type; }

  /**
   * Sets the type for the weapon proficiency.
   * @param type Type name to set.
   */
  public void setType(String type) { this.type = type; }

  /**
   * @return The skill associated with the weapon proficiency.
   */
  public String getSkill() { return skill; }

  /**
   * Sets the skill associated with the weapon proficiency.
   * @param skill Skill name to set.
   */
  public void setSkill(String skill) { this.skill = skill; }

  /**
   * @return The damage types for weapons that have the proficiency.
   */
  public Collection<String> getDamageTypes() { return Collections.unmodifiableCollection(damageTypes); }

  /**
   * Adds a damage type for weapons that have the proficiency.
   * @param type Name for the type of damage to add.
   */
  public void addDamageType(String type) { damageTypes.add(type); }

  /**
   * @return Number of hands needed to wield weapons with this proficiency.
   */
  public int getHands() { return hands; }

  /**
   * Sets the number of hands needed to wield weapons with this proficiency.
   * @param hands Number of hands to set.
   */
  public void setHands(int hands) { this.hands = hands; }
}
