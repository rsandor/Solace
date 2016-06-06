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
public class Slash extends CooldownCommand {
  static final int POTENCY = 150;
  static final int COMBO_POTENCY = 225;

  public Slash(Player p) {
    super("slash", p);
    setCooldownDuration(CooldownCommand.GLOBAL_COOLDOWN);
    setInitiatesCombat(true);
  }

  public boolean execute(int level, Player target) {
    if (!player.isFighting() && target == null) {
      player.sendln("Who would you like to slash?");
      return false;
    }

    if (player.isFighting() && target == null) {
      target = BattleManager.getBattleFor(player).getTargetFor(player);
    }

    boolean isCombo = player.getComboAction().equals("flurry");
    int potency = isCombo ? COMBO_POTENCY : POTENCY;

    AttackResult result = Battle.rollToHit(player, target, potency);
    if (result == AttackResult.MISS) {
      player.sendMessage("Your {mslash{x misses!");
      return false;
    }

    boolean critical = result == AttackResult.CRITICAL;
    int damage = Battle.rollDamage(player, target, critical, potency);
    target.applyDamage(damage);

    if (isCombo) {
      player.sendln(String.format(
        "[{g%d{x] {y<{Ycombo{y>{x Your {mslash{x hits %s!",
        damage, target.getName()));
    } else {
      player.sendln(String.format(
        "[{g%d{x] Your {mslash{x hits %s!", damage, target.getName()));
    }

    target.sendln(String.format(
      "<{r%d{x> %s hits you with a {mslash{x!", damage, player.getName()));

    return true;
  }
}
