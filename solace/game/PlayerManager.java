package solace.game;
import solace.util.*;

/**
 * Manager for handling player states.
 * @author Ryan Sandor Richards
 */
public class PlayerManager {
  static Clock.Event event = null;

  /**
   * Cleans the scenario where a player is in the fighting state but is not
   * associated with any battles.
   * @param p The player to check and clean.
   */
  private static void cleanFightingState(Player p) {
    boolean hasBattle = BattleManager.getBattleFor(p) != null;
    boolean isFighting = p.isFighting();
    if (isFighting && !hasBattle) {
      Log.debug(String.format(
        "Cleaning %s fighting state (not in battle)", p.getName()));
      p.setStanding();
    }
  }

  /**
   * Cleans up the scenario where a player is dead but has not been cast into
   * the dead state.
   * @param p The player to check and clean.
   */
  private static void cleanDeathState(Player p) {
    if (p.isDead() && p.getPlayState() != PlayState.DEAD) {
      Log.debug(String.format(
        "Cleaning %s death state (should be dead)", p.getName()));
      p.die(null);
    }
  }

  /**
   * Handles cleanup for player states when data get corrupted due to server
   * crashes, buggy code, etc.
   */
  private static void cleanupPlayers() {
    for (Player p : Game.getAllPlayers()) {
      p.removeExpiredBuffs();
      PlayerManager.cleanFightingState(p);
      PlayerManager.cleanDeathState(p);
    }
  }

  /**
   * Initializes and starts the player manager.
   */
  public static void start() {
    if (event != null) { return; }
    Log.info("Starting player manager");
    event = Clock.getInstance().interval("player-cleanup", 2, new Runnable() {
      public void run() { PlayerManager.cleanupPlayers(); }
    });
  }

  /**
   * Stops the player manager.
   */
  public static void stop() {
    if (event == null) { return; }
    Log.info("Stopping player manager");
    event.cancel();
    event = null;
  }
}
