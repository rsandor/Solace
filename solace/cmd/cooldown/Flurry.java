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
  static final int POTENCY = 150;

  public Flurry(Player p) {
    super("flurry", p);
    setCooldownDuration(CooldownCommand.GLOBAL_COOLDOWN);
    setInitiatesCombat(true);
    addResourceCost(new SpCost(2));
  }

  public boolean execute(int level, Player target) {
    if (!player.isFighting() && target == null) {
      player.sendln("Who would you like to attack with a flurry of blows?");
      return false;
    }

    if (player.isFighting() && target == null) {
      target = BattleManager.getBattleFor(player).getTargetFor(player);
    }

    AttackResult result = Battle.rollToHit(player, target, POTENCY);
    if (result == AttackResult.MISS) {
      player.sendMessage("Your {mflurry of blows{x misses!");
      return false;
    }

    boolean critical = result == AttackResult.CRITICAL;
    int damage = Battle.rollDamage(player, target, critical, POTENCY);
    target.applyDamage(damage);

    player.sendln(String.format(
      "[{g%d{x] Your {mflurry of blows{x hits %s!",
      damage, target.getName()));
    target.sendln(String.format(
      "<{r%d{x> %s hits you with a {mflurry of blows{x!",
      damage, player.getName()));

    return true;
  }
}
