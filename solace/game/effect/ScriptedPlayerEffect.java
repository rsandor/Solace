package solace.game.effect;

/**
 * Abstract base class for all player effects.
 * @author Ryan Sandor Richards
 */
public class ScriptedPlayerEffect implements PlayerEffect {
  private PlayerModifier<Double> hpRecovery = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> mpRecovery = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> spRecovery = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> strengthMod = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> magicMod = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> vitalityMod = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> speedMod = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> hpCostMod = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> mpCostMod = new PlayerModifierIdentity<>();
  private PlayerModifier<Double> spCostMod = new PlayerModifierIdentity<>();

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

  @Override
  public void modHpCost(PlayerModifier<Double> callback) {
    hpCostMod = callback;
  }

  @Override
  public PlayerModifier<Double> getModHpCost() {
    return hpCostMod;
  }

  @Override
  public void modMpCost(PlayerModifier<Double> callback) {
    mpCostMod = callback;
  }

  @Override
  public PlayerModifier<Double> getModMpCost() {
    return mpCostMod;
  }

  @Override
  public void modSpCost(PlayerModifier<Double> callback) {
    spCostMod = callback;
  }

  @Override
  public PlayerModifier<Double> getModSpCost() {
    return spCostMod;
  }

  @Override
  public void modStrength(PlayerModifier<Double> callback) {
    strengthMod = callback;
  }

  @Override
  public PlayerModifier<Double> getModStrength() {
    return strengthMod;
  }

  @Override
  public void modMagic(PlayerModifier<Double> callback) {
    magicMod = callback;
  }

  @Override
  public PlayerModifier<Double> getModMagic() {
    return magicMod;
  }

  @Override
  public void modVitality(PlayerModifier<Double> callback) {
    vitalityMod = callback;
  }

  @Override
  public PlayerModifier<Double> getModVitality() {
    return vitalityMod;
  }

  @Override
  public void modSpeed(PlayerModifier<Double> callback) {
    speedMod = callback;
  }

  @Override
  public PlayerModifier<Double> getModSpeed() {
    return speedMod;
  }
}
