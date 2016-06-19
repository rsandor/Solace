package solace.game;

import java.util.*;
import solace.util.*;

/**
 * Manages the recovery of resources for all mobiles and characters in the game
 * world.
 *
 * - TODO Add mobile recovery
 *
 * @author Ryan Sandor Richards
 */
public class RecoveryManager {
  static Clock.Event recoveryEvent = null;

  /**
   * Initializes and starts the battle manager.
   */
  public static void start() {
    if (recoveryEvent != null) { return; }
    Log.info("Starting recovery manager");
    int ticks = Integer.parseInt(Config.get("world.recovery.ticks"));
    recoveryEvent = Clock.getInstance().interval(
      "recovery-cycle",
      ticks,
      new Runnable() {
        public void run() { RecoveryManager.cycle(); }
      }
    );
  }

  /**
   * Stops the battle manager.
   */
  public static void stop() {
    if (recoveryEvent == null) { return; }
    Log.info("Stopping recovery manager");
    recoveryEvent.cancel();
    recoveryEvent = null;
  }

  /**
   * Performs a recovery cycle for all characters and mobiles in the game world.
   */
  public static void cycle() {
    Collection<solace.game.Character> playing = World.getActiveCharacters();
    synchronized(playing) {
      for (solace.game.Character p : playing) {
        // Fighting players do not recover resources
        if (p.isFighting()) {
          continue;
        }

        // Determine base recovery mod via play state
        double recoveryMod = 0.05;
        if (p.isSitting()) {
          recoveryMod = 0.08;
        } else if (p.isResting()) {
          recoveryMod = 0.15;
        } else if (p.isSleeping()) {
          recoveryMod = 0.25;
        }

        // Add additional modifier for vitality
        recoveryMod += 0.25 * (p.getVitality() / 500);

        // Perform the recovery for all resources
        int maxHp = p.getMaxHp();
        int hp = p.getHp();
        int recoveredHp = Math.max(1, (int)(maxHp * recoveryMod));
        p.setHp(Math.min(maxHp, hp + recoveredHp));

        int maxMp = p.getMaxMp();
        int mp = p.getMp();
        int recoveredMp = (p.hasPassive("meditation")) ?
          Math.max(1, (int)(maxMp * recoveryMod * 1.25)) :
          Math.max(1, (int)(maxMp * recoveryMod));
        p.setMp(Math.min(maxMp, mp + recoveredMp));

        int maxSp = p.getMaxSp();
        int sp = p.getSp();
        int recoveredSp = (p.hasPassive("meditation")) ?
          Math.max(1, (int)(maxSp * recoveryMod * 1.25)) :
          Math.max(1, (int)(maxSp * recoveryMod));
        p.setSp(Math.min(maxSp, sp + recoveredSp));
      }
    }
  }
}
