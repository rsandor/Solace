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

  /**
   * Register a modifier callback that effects the player's strength ability score.
   * @param callback Modifier callback to execute when calculating player strength.
   */
  void modStrength (PlayerModifier<Double> callback);

  /**
   * @return The strength ability score modifier. If not set this returns an identity.
   */
  PlayerModifier<Double> getModStrength ();

  /**
   * Register a modifier callback that effects the player's magic ability score.
   * @param callback Modifier callback to execute when calculating player magic.
   */
  void modMagic (PlayerModifier<Double> callback);

  /**
   * @return The magic ability score modifier. If not set this returns an identity.
   */
  PlayerModifier<Double> getModMagic ();

  /**
   * Register a modifier callback that effects the player's vitality ability score.
   * @param callback Modifier callback to execute when calculating player vitality.
   */
  void modVitality (PlayerModifier<Double> callback);

  /**
   * @return The vitality ability score modifier. If not set this returns an identity.
   */
  PlayerModifier<Double> getModVitality ();

  /**
   * Register a modifier callback that effects the player's speed ability score.
   * @param callback Modifier callback to execute when calculating player speed.
   */
  void modSpeed (PlayerModifier<Double> callback);

  /**
   * @return The speed ability score modifier. If not set this returns an identity.
   */
  PlayerModifier<Double> getModSpeed ();
}
