package solace.cmd.cooldown;

import solace.game.*;

/**
 * Applies the "concentrate" buff that doubles the potency of cooldon actions
 * for ten seconds.
 * @author Ryan Sandor Richards
 */
public class Concentrate extends CooldownCommand {
  public Concentrate(Player p) {
    super("concentrate", p);
    setCooldownDuration(120);
  }

  public boolean execute(int level, Player target) {
    player.applyBuff("concentrating");
    return true;
  }
}
