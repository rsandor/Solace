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
    boolean isHit = executePhysicalAttack(target, POTENCY);
    if (isHit) {
      target.applyBuff(Buffs.create("stunned", 4));
    }
    return isHit;
  }
}
