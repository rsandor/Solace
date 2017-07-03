package solace.cmd.cooldown;

import solace.game.*;

/**
 * Cooldown that allows a player to "vanish" from sight. They gain the
 * "vanished" buff until they move, initiate combat, or are attacked (but there
 * are very few entities that can see a halfling who vanishes).
 *
 * If used while in combat the player is immediately removed from the battle.
 *
 * @author Ryan Sandor Richards
 */
public class Vanish extends CooldownCommand {
  protected static final int COOLDOWN_DURATION = 180;

  public Vanish(Player p) {
    super("vanish", p);
    setCooldownDuration(COOLDOWN_DURATION);
    setInitiatesCombat(false);
  }

  public boolean execute(int level, Player target) {
    // Apply the "vanished buff"
    player.applyBuff("vanished");

    // Stop battle (if applicable)
    Battle battle = BattleManager.getBattleFor(player);
    if (battle != null) {
      battle.remove(player);
    }

    return true;
  }
}
