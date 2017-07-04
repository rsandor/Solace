package solace.cmd.cooldown;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * Potency 200 attack spell that heals 5-10% of MP (based on skill level).
 * @author Ryan Sandor Richards
 */
public class Icespike extends CooldownCommand {
  public Icespike(Player p) {
    super("icespike", p);
    setCooldownDuration(CooldownCommand.GLOBAL_COOLDOWN);
    setInitiatesCombat(true);
    setBasePotency(200);
    setSavingThrow("prudence");
  }

  private void healMp (int level) {
    double pctHeal = 0.05 + (0.05 * level / 100.0);
    int mpHeal = (int)(player.getMaxMp() * pctHeal);
    player.setMp(Math.min(
      player.getMaxMp(),
      player.getMp() + mpHeal));
  }

  public boolean execute(int level, Player target) {
    boolean isHit = executeAttack(target);
    if (isHit) {
      healMp(level);
    }
    return isHit;
  }
}
