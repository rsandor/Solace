package solace.cmd.cooldown;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * Potency 800 attack spell that costs 20% MP.
 * @author Ryan Sandor Richards
 */
public class Flamestrike extends CooldownCommand {
  static final int POTENCY = 800;
  static final String SAVE = "reflex";

  public Flamestrike(Player p) {
    super("flamestrike", p);
    setCooldownDuration(CooldownCommand.GLOBAL_COOLDOWN);
    setInitiatesCombat(true);
    addResourceCost(new MpCost(AbstractResourceCost.CostType.PERCENTAGE, 20));
  }

  public boolean execute(int level, Player target) {
    return executeMagicAttack(target, POTENCY, SAVE);
  }
}
