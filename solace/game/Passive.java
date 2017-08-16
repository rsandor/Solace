package solace.game;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import solace.game.effect.PlayerEffect;
import solace.game.effect.ScriptedPlayerEffect;

/**
 * Represents a passive ability in the game world.
 * @author Ryan Sandor Richards
 */
public class Passive {
  private String name = "";
  private String label = "";
  private PlayerEffect effect = new ScriptedPlayerEffect();

  /**
   * Creates a new passive with the given name.
   * @param name Name of the passive.
   */
  public Passive(String name) {
    setName(name);
  }

  /**
   * @return The name of the passive.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name for the passive.
   * @param name The name to set for the passive.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The game play display label for the passive.
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the game play display label for the passive.
   * @param label Label to set for the passive.
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * @return The player effect for this passive.
   */
  public PlayerEffect getEffect() {
    return effect;
  }

  /**
   * Sets the underlying player effect for this passive.
   * @param effect The effect to set.
   */
  public void setEffect(PlayerEffect effect) {
    this.effect = effect;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Passive)) {
      return false;
    } else if (obj == this) {
      return true;
    }
    Passive other = (Passive)obj;
    return new EqualsBuilder().append(name, other.getName()).isEquals();
  }
}
