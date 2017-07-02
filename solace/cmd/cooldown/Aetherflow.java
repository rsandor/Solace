package solace.cmd.cooldown;

import solace.game.*;

/**
 * The aetherflow cooldown instantly heals 50% of MP with a cooldown duration of
 * 300 seconds.
 * @author Ryan Sandor Richards
 */
public class Aetherflow extends CooldownCommand {
  protected static final int COOLDOWN_DURATION = 300;

  public Aetherflow(Player p) {
    super("aetherflow", p);
    setCooldownDuration(COOLDOWN_DURATION);
    setInitiatesCombat(false);
  }

  public boolean execute(int level, Player target) {
    int mpHealed = Math.min(
      player.getMaxMp() / 2,
      player.getMaxMp() - player.getMp());

    player.setMp(Math.min(
      player.getMaxMp(),
      player.getMp() + mpHealed));

    player.sendln(String.format(
      "You draw upon the {maetherflow{x! You've been healed [{M%d{x] mp!",
      mpHealed
    ));

    return true;
  }
}
