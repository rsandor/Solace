package solace.game;

import java.io.*;
import java.util.*;

import solace.io.Areas;
import solace.io.Buffs;
import solace.io.Races;
import solace.io.Skills;
import solace.net.Connection;
import solace.cmd.GameException;
import solace.util.Log;

/**
 * Holds the state of the entire game world.
 * @author Ryan Sandor Richards (Gaius)
 */
public class World {
  private static boolean initialized = false;

  /**
   * Initializes the game world: loads areas and sets up collections.
   * @throws GameException If the default room could not be found after areas
   *   are loaded.
   */
  static void init() throws GameException, IOException {
    if (initialized)
      return;
    initialized = true;
  }
}
