package solace.cmd.cooldown;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * "Slash" is a cooldown action that does 150 potency damage to the attacker's
 * target. If executed after the "flurry of blows" attack it has a combo potency
 * of 250.
 * @author Ryan Sandor Richards
 */
public class Slash extends CooldownCommand {
  public Slash(Player p) {
    super("slash", p);
    setCooldownDuration(CooldownCommand.GLOBAL_COOLDOWN);
    setInitiatesCombat(true);
    setBasePotency(150);
    setComboPotency(225);
    setCombosWith("flurry");
    addResourceCost(new SpCost(4));
  }

  public boolean execute(int level, Player target) {
    return executeAttack(target);
  }
}
