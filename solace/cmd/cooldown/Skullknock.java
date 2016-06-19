package solace.cmd.cooldown;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * A potency 150 attack that stuns the target for 4 seconds.
 * @author Ryan Sandor Richards
 */
public class Skullknock extends CooldownCommand {
  static final int POTENCY = 150;
  static final int COOLDOWN_DURATION = 180;

  public Skullknock(Player p) {
    super("skullknock", p);
    setCooldownDuration(COOLDOWN_DURATION);
    setInitiatesCombat(true);
  }

  public boolean execute(int level, Player target) {
    if (!player.isFighting() && target == null) {
      player.sendln("Who's skull would you like to knock?");
      return false;
    }

    if (player.isFighting() && target == null) {
      target = BattleManager.getBattleFor(player).getTargetFor(player);
    }

    AttackResult result = Battle.rollToHit(player, target, POTENCY);
    if (result == AttackResult.MISS) {
      player.sendMessage("Your {mskullknock{x misses!");
      return false;
    }

    boolean critical = (result == AttackResult.CRITICAL);
    int damage = Battle.rollDamage(player, target, critical, POTENCY);
    target.applyDamage(damage);
    target.applyBuff(Buffs.create("stun", 4));

    player.sendln(String.format(
      "[{g%d{x] Your {mskullknock{x hits %s!",
      damage, target.getName()));
    target.sendln(String.format(
      "<{r%d{x> %s hits you with a {mskullknock{x!",
      damage, player.getName()));

    return true;
  }
}
