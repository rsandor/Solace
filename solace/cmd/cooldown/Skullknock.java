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
  public Skullknock(Player p) {
    super("skullknock", p);
    setCooldownDuration(180);
    setBasePotency(150);
    setInitiatesCombat(true);
  }

  public boolean execute(int level, Player target) {
    boolean isHit = executeAttack(target);
    if (isHit) {
      target.applyBuff(Buffs.create("stunned", 4));
    }
    return isHit;
  }
}
