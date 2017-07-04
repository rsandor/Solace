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
  public Flamestrike(Player p) {
    super("flamestrike", p);
    setCooldownDuration(CooldownCommand.GLOBAL_COOLDOWN);
    setInitiatesCombat(true);
    setBasePotency(800);
    setSavingThrow("reflex");
    addResourceCost(new MpCost(20));
  }

  public boolean execute(int level, Player target) {
    return executeAttack(target);
  }
}
