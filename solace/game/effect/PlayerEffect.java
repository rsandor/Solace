package solace.game.effect;

/**
 * Represents behaviors of all classes that can be scripted to effect a player in some way.
 * @author Ryan Sandor Richards.
 */
public interface PlayerEffect {
  /**
   * Register a modifier callback that effects the amount of HP recovered during a cycle of the
   * recovery manager.
   * @param callback Modifier callback to run during recovery manager cycle.
   */
  void modHpRecovery (PlayerModifier<Double> callback);

  /**
   * @return The HP recovery modifier for this effect. Returns the identity function if modifier
   *   has been registered to modify HP recovery.
   */
  PlayerModifier<Double> getModHpRecovery ();

  /**
   * Register a modifier callback that effects the amount of MP recovered during a cycle of the
   * recovery manager.
   * @param callback Modifier callback to run during recovery manager cycle.
   */
  void modMpRecovery (PlayerModifier<Double> callback);

  /**
   * @return The MP recovery modifier for this effect. Returns the identity function if modifier
   *   has been registered to modify MP recovery.
   */
  PlayerModifier<Double> getModMpRecovery ();

  /**
   * Register a modifier callback that effects the amount of SP recovered during a cycle of the
   * recovery manager.
   * @param callback Modifier callback to run during recovery manager cycle.
   */
  void modSpRecovery (PlayerModifier<Double> callback);

  /**
   * @return The SP recovery modifier for this effect. Returns the identity function if modifier
   *   has been registered to modify SP recovery.
   */
  PlayerModifier<Double> getModSpRecovery ();
}
