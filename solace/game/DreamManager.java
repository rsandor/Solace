package solace.game;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import solace.io.Config;
import solace.io.Dreams;
import solace.util.Clock;
import solace.util.Log;
import solace.util.Roll;

import java.util.Arrays;
import java.util.stream.Stream;

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
    Game.getActiveCharacters().stream().filter(AbstractPlayer::isSleeping).forEach((c) -> {
      // Determine if we present the player with a dream
      if (Roll.uniform() <= 0.25) {
        // Fetch a random dream and blank out random words
        Stream<String> words = Arrays.stream(Dreams.getInstance().getRandom().split("\\s"));
        c.wrapln(Joiner.on(" ").join(
          words.map(word -> Roll.uniform() < 0.167 ? word.replaceAll(".", "_") : word).toArray()));
        c.sendMessage("");
      } else {
        c.sendMessage("You are soundly asleep.");
      }
    });
  }
}