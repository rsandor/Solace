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
  static final int POTENCY = 150;
  static final int COMBO_POTENCY = 225;

  public Slash(Player p) {
    super("slash", p);
    setCooldownDuration(CooldownCommand.GLOBAL_COOLDOWN);
    setInitiatesCombat(true);
    addResourceCost(new SpCost(4));
  }

  public boolean execute(int level, Player target) {
    return executePhysicalAttack(target, POTENCY, "flurry", COMBO_POTENCY);
  }
}
