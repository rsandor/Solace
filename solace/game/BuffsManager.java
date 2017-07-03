package solace.game;

import solace.util.*;

/**
 * Manages player buff expiration.
 * @author Ryan Sandor Richards
 */
public class BuffsManager {
  static Clock.Event cleanupEvent = null;

  /**
   * Triggers buff removal for all expired buffs for every character and mobile
   * in the game world.
   */
  private static void removeAllExpiredBuffs() {
    Log.trace("Removing expired buffs from all players and mobiles.");
    MobileManager.removeExpiredBuffs();
    World.removeExpiredPlayerBuffs();
  }

  /**
   * Initializes and starts the buffs manager.
   */
  public static void start() {
    if (cleanupEvent != null) { return; }
    Log.info("Starting buffs manager");
    cleanupEvent = Clock.getInstance().interval("buff-cleanup", 1, new Runnable() {
      public void run() { BuffsManager.removeAllExpiredBuffs(); }
    });
  }

  /**
   * Stops the buffs manager.
   */
  public static void stop() {
    if (cleanupEvent == null) { return; }
    Log.info("Stopping buffs manager");
    cleanupEvent.cancel();
    cleanupEvent = null;
  }
}
