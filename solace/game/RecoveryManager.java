package solace.game;

import solace.game.effect.PlayerEffect;
import solace.io.Config;
import solace.util.*;

/**
 * Manages the recovery of resources for all mobiles and players in the game
 * world.
 *
 * - TODO Add mobile recovery
 *
 * @author Ryan Sandor Richards
 */
public class RecoveryManager {
  private static Clock.Event recoveryEvent = null;

  /**
   * Initializes and starts the battle manager.
   */
  public static void start() {
    if (recoveryEvent != null) { return; }
    Log.info("Starting recovery manager");
    String ticksString = Config.get("game.recovery.ticks");
    int ticks = ticksString != null ? Integer.parseInt(ticksString) : 10;
    recoveryEvent = Clock.getInstance().interval(
      "recovery-cycle", ticks, RecoveryManager::cycle);
  }

  /**
   * Stops the recovery manager.
   */
  public static void stop() {
    if (recoveryEvent == null) { return; }
    Log.info("Stopping recovery manager");
    recoveryEvent.cancel();
    recoveryEvent = null;
  }

  /**
   * Performs a recovery cycle for all players and mobiles in the game world.
   */
  private static void cycle() {
    // TODO This will need to be updated to heal mobiles as well
    Game.getActiveCharacters().forEach(player -> {
      // Fighting players do not,  recover resources
      if (player.isFighting()) {
        return;
      }

      // Determine base recovery mod via play state
      double recoveryMod = 0.05;
      if (player.isSitting()) {
        recoveryMod = 0.08;
      } else if (player.isResting()) {
        recoveryMod = 0.15;
      } else if (player.isSleeping()) {
        recoveryMod = 0.25;
      }

      // Add additional modifier for vitality
      recoveryMod += 0.1 * (player.getVitality() / 500);

      // Determine base recovery for all resources before effects
      int maxHp = player.getMaxHp();
      int hp = player.getHp();
      double recoveredHp = maxHp * recoveryMod;

      int maxMp = player.getMaxMp();
      int mp = player.getMp();
      double recoveredMp = maxMp * recoveryMod;

      int maxSp = player.getMaxSp();
      int sp = player.getSp();
      double recoveredSp = maxSp * recoveryMod;

      // Apply modifications effects for passives
      for(Passive passive : player.getPassives()) {
        PlayerEffect effect = passive.getEffect();
        recoveredHp = effect.getModHpRecovery().modify(player, recoveredHp);
        recoveredMp = effect.getModMpRecovery().modify(player, recoveredMp);
        recoveredSp = effect.getModSpRecovery().modify(player, recoveredSp);
      }

      // Perform recovery on the player
      player.setHp((int)Math.min(maxHp, hp + recoveredHp));
      player.setMp((int)Math.min(maxMp, mp + recoveredMp));
      player.setSp((int)Math.min(maxSp, sp + recoveredSp));
    });
  }
}
