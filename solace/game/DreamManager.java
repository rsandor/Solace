package solace.game;

import solace.io.Config;
import solace.util.Clock;
import solace.util.Log;

/**
 * Manages dreams for all sleeping characters in the game world.
 * @author Ryan Sandor Richards
 */
public class DreamManager {
  /**
   * Clock event used by the dream manager.
   */
  private static Clock.Event dreamEvent = null;

  /**
   * Starts the character dream manager.
   */
  public static void start() {
    if (dreamEvent != null) return;
    Log.info("Starting dream manager");
    int ticks = 2 * Integer.parseInt(Config.get("game.recovery.ticks", "12"));
    dreamEvent = Clock.getInstance().interval("dream-cycle", ticks, DreamManager::cycle);
  }

  /**
   * Stops the character dream manager.
   */
  public static void stop() {
    if (dreamEvent == null) return;
    Log.info("Stopping dream manager");
    dreamEvent.cancel();
    dreamEvent = null;
  }

  /**
   * Performs a dream cycle for all sleeping players.
   */
  private static void cycle() {
    Game.getActiveCharacters().stream().filter((c) -> c.isSleeping()).forEach((c) -> {
      c.sendMessage("You continue to sleep blissfully.");
    });
  }
}