package solace.game.effect;

import solace.game.Player;

/**
 * Functional interface that represents a single valued modifier callback for player effects.
 * @param <T> Type of the value to be modified.
 */
public interface PlayerModifier<T> {
  /**
   * Modifies a player value. This could be a resource stat such as HP, damage from
   * an attack, etc.
   * @param player The player being effected.
   * @param value The value to be modified.
   * @return The modified value.
   */
  T modify (Player player, T value);
}
