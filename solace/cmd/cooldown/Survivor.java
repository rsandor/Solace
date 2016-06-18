package solace.cmd.cooldown;

import solace.game.*;

/**
 * The survivor cooldown instantly heals 50% of both HP and SP with a cooldown
 * duration of 300 seconds.
 * @author Ryan Sandor Richards
 */
public class Survivor extends CooldownCommand {
  protected static final int COOLDOWN_DURATION = 300;

  public Survivor(Player p) {
    super("survivor", p);
    setCooldownDuration(COOLDOWN_DURATION);
    setInitiatesCombat(false);
  }

  public boolean execute(int level, Player target) {
    int hpHealed = Math.min(
      player.getMaxHp() / 2,
      player.getMaxHp() - player.getHp());
    int spHealed = Math.min(
      player.getMaxSp() / 2,
      player.getMaxSp() - player.getSp());

    player.setHp(player.getHp() + hpHealed);
    player.setSp(player.getSp() + spHealed);

    player.sendln(String.format(
      "You are a {msurvivor{x! You've been healed [{G%d{x] hp and [{Y%d{x] sp!",
      hpHealed, spHealed
    ));

    return true;
  }
}
