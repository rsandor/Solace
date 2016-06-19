package solace.cmd.cooldown;

import solace.game.*;

/**
 * Applies the "concentrate" buff that doubles the potency of cooldon actions
 * for ten seconds.
 * @author Ryan Sandor Richards
 */
public class Concentrate extends CooldownCommand {
  protected static final int COOLDOWN_DURATION = 120;

  public Concentrate(Player p) {
    super("concentrate", p);
    setCooldownDuration(COOLDOWN_DURATION);
    setInitiatesCombat(false);
  }

  public boolean execute(int level, Player target) {
    player.sendln("You enter into a state of total concentration.");
    player.applyBuff("concentrate");
    return true;
  }
}
