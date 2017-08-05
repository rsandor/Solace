package solace.game;

import solace.io.AssetNotFoundException;
import solace.io.DamageTypes;
import solace.util.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents damage from a single source in the game world.
 * @author Ryan Sandor Richards
 */
public class Damage<S> {
  private double amount;
  private Set<DamageType> damageTypes = new HashSet<>();
  private Player target;
  private S source;

  /**
   * Creates a new damage model.
   * @param a The amount of damage.
   * @param g The target for the damage.
   * @param s The source of the damage.
   */
  public Damage(double a, Player g, S s) {
    amount = a;
    target = g;
    source = s;
  }

  /**
   * @return The amount of damage to be done.
   */
  public double getAmount() { return amount; }

  /**
   * Adds the given value to the damage amount.
   * @param value Value to add.
   */
  public void add(double value) { amount += value; }

  /**
   * Multiplies the damage amount by the given value.
   * @param value Value by which to multiply.
   */
  @SuppressWarnings("unused")
  public void mult(double value) { amount *= value; }

  /**
   * @return An unmodifiable set of the types of damage being dealt.
   */
  public Set<DamageType> getTypes() {
    return Collections.unmodifiableSet(damageTypes);
  }

  /**
   * Adds a damage type to the damage.
   * @param damageTypeName Name of the damage type to add.
   */
  @SuppressWarnings("unused")
  public void addType(String damageTypeName) {
    try {
      addType(DamageTypes.getInstance().get(damageTypeName));
    } catch (AssetNotFoundException e) {
      Log.warn(String.format("Invalid type '%s' referenced in Damage.addType", damageTypeName));
    }
  }

  /**
   * Adds a damage type to the damage.
   * @param type The damage type to add.
   */
  public void addType(DamageType type) {
    damageTypes.add(type);
  }

  /**
   * Determines if this damage has the given type.
   * @param damageTypeName Name of the damage type to check.
   * @return True if it has the type, false if it does not.
   */
  @SuppressWarnings("unused")
  public boolean hasType(String damageTypeName) {
    try {
      return hasType(DamageTypes.getInstance().get(damageTypeName));
    } catch (AssetNotFoundException e) {
      Log.warn(String.format("Invalid type '%s' referenced in Damage.isOfType", damageTypeName));
      return false;
    }
  }

  /**
   * Determines if this damage has the given type.
   * @param type Type to check.
   * @return True if the damage has the given type, false otherwise.
   */
  public boolean hasType(DamageType type) {
    return damageTypes.contains(type);
  }

  /**
   * @return True if this damage has a physical type.
   */
  public boolean isPhysical() {
    return damageTypes.stream().anyMatch(DamageType::isPhysical);
  }

  /**
   * @return True if this damage has a magical type.
   */
  public boolean isMagical() {
    return damageTypes.stream().anyMatch(DamageType::isMagical);
  }

  /**
   * @return The target that will take the damage.
   */
  public Player getTarget() { return target; }

  /**
   * @return The source of the damage.
   */
  public S getSource() { return source; }

}
