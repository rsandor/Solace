package solace.cmd.cooldown;
import solace.game.*;
import solace.cmd.InvalidTargetException;

/**
 * Applies the "shocked" debuff to a target.
 * @author Ryan Sandor Richards
 */
public class Shock extends CooldownCommand {
  private static final double POTENCY_LOW = 20.0;
  private static final double POTENCY_HIGH = 30.0;
  private static final int ATTACK_ROLL_POTENCY = 100;

  public Shock(Player p) {
    super("shock", p);
    setInitiatesCombat(true);
    setCooldownDuration(6);
    setCastTime(3);
    setBasePotency(ATTACK_ROLL_POTENCY);
    addResourceCost(new MpCost(5));
    setSavingThrow("will");
  }

  /**
   * @see solace.cmd.cooldown.CooldownCommand
   */
  protected String getCastMessage() {
    return "You begin casting shock...";
  }

  public boolean execute(int level, Player givenTarget) {
    try {
      Player target = resolveTarget(givenTarget);
      AttackResult result = rollToHit(target);
      if (result.isMiss()) {
        sendMissMessage();
        return false;
      }

      // TODO Seems like this calculation should be centralized...
      // TODO Should we allow for the use of passives on this?
      double potency = POTENCY_LOW + POTENCY_HIGH * (level/100.0);
      int avgDamage = (int)(potency * player.getAverageDamage() / 100.0);

      target.applyBuff(new DotBuff(
        "shocked", target, avgDamage, 30, 2,
        "<{r%d{x> you are shocked by the envoloping electricity!"));

      return true;
    } catch (InvalidTargetException ite) {
      player.sendln(ite.getMessage());
      return false;
    }
  }
}
