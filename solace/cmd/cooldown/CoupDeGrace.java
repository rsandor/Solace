package solace.cmd.cooldown;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * "Flurry of blows" is a cooldown action that does 150 potency damage to the
 * attacker's target.
 * @author Ryan Sandor Richards
 */
public class CoupDeGrace extends CooldownCommand {
  static final int POTENCY = 1000;

  public CoupDeGrace(Player p) {
    super("coup", p);
    setCooldownDuration(120);
    setInitiatesCombat(true);
  }

  public boolean execute(int level, Player target) {
    if (!player.isFighting() && target == null) {
      player.sendln("Who would you like to coup de grace?");
      return false;
    }

    if (player.isFighting() && target == null) {
      target = BattleManager.getBattleFor(player).getTargetFor(player);
    }

    if ((double)target.getHp() / (double)target.getMaxHp() >= 0.3) {
      player.sendln(
        "Coup de grace can only be used on targets with less than 30% health.");
      return false;
    }

    AttackResult result = Battle.rollToHit(player, target, POTENCY);
    if (result == AttackResult.MISS) {
      player.sendMessage("Your {mriposte{x misses!");
      return false;
    }

    boolean critical = result == AttackResult.CRITICAL;
    int damage = Battle.rollDamage(player, target, critical, POTENCY);
    target.applyDamage(damage);

    player.sendln(String.format(
      "[{g%d{x] Your {mcoup de grace{x hits %s!",
      damage, target.getName()));
    target.sendln(String.format(
      "<{r%d{x> %s hits you with a {mcoup de grace{x!",
      damage, player.getName()));

    return true;
  }
}
