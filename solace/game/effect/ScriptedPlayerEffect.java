package solace.game.effect;

/**
 * Abstract base class for all player effects.
 * @author Ryan Sandor Richards
 */
public class ScriptedPlayerEffect implements PlayerEffect {
  private PlayerModifier<Double> hpRecovery = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> mpRecovery = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> spRecovery = new PlayerModifierIdentity<>();

  @Override
  public void modHpRecovery (PlayerModifier<Double> callback) {
    hpRecovery = callback;
  }

  @Override
  public PlayerModifier<Double> getModHpRecovery() {
    return hpRecovery;
  }

  @Override
  public void modMpRecovery (PlayerModifier<Double> callback) {
    mpRecovery = callback;
  }

  @Override
  public PlayerModifier<Double> getModMpRecovery() {
    return mpRecovery;
  }

  @Override
  public void modSpRecovery (PlayerModifier<Double> callback) {
    spRecovery = callback;
  }

  @Override
  public PlayerModifier<Double> getModSpRecovery() {
    return spRecovery;
  }
}
