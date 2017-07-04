package solace.cmd.cooldown;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;
import solace.cmd.InvalidTargetException;

/**
 * "Coup de Grace" is a cooldown action that does 1000 potency damage to a
 * target whos health is at 30% or less.
 * @author Ryan Sandor Richards
 */
public class CoupDeGrace extends CooldownCommand {
  public CoupDeGrace(Player p) {
    super("coup", p);
    setDisplayName("coup de grace");
    setCooldownDuration(120);
    setInitiatesCombat(true);
    setBasePotency(1000);
    addResourceCost(new SpCost(10));
  }

  protected void checkValidTarget(Player target) throws InvalidTargetException {
    super.checkValidTarget(target);
    boolean isValid = (double)target.getHp() / (double)target.getMaxHp() < 0.3;
    if (!isValid) {
      throw new InvalidTargetException(String.format(
        "%s can only be used on targets with less than 30% health.",
        getDisplayName()));
    }
  }

  public boolean execute(int level, Player target) {
    return executeAttack(target);
  }
}
