package solace.game.effect;

import solace.game.Player;

/**
 * Identity player modifier. This is used when an effect does not register an explicit modifier.
 * @param <T> Type for the value to be modified.
 */
public class PlayerModifierIdentity<T> implements PlayerModifier<T> {
  @Override
  public T modify(Player player, T value) {
    return value;
  }
}
