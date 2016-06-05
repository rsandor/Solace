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
public class Flurry extends CooldownCommand {
  public Flurry(Player p) {
    super("flurry", p, CooldownCommand.GLOBAL_COOLDOWN, true);
  }

  public boolean execute(int level, Player target) {
    if (!player.isFighting() && target == null) {
      player.sendln("Who would you like to attack with a flurry of blows?");
      return false;
    }

    if (player.isFighting() && target == null) {
      target = BattleManager.getBattleFor(player).getTargetFor(player);
    }

    AttackResult result = Battle.rollToHit(player, target, 150);
    if (result == AttackResult.MISS) {
      player.sendMessage("Your flurry of blows misses!");
      return false;
    }

    boolean critical = result == AttackResult.CRITICAL;
    int damage = Battle.rollDamage(player, target, critical, 150);
    target.applyDamage(damage);

    player.sendMessage(String.format(
      "[{g%d{x] Your flurry of blows hits %s!\n\r",
      damage, target.getName()));

    target.sendMessage(String.format(
      "<{r%d{x> %s hits you with a flurry of blows!\n\r",
      damage, player.getName()));

    return true;
  }
}
