package solace.game;

import solace.net.*;
import solace.util.*;
import solace.xml.*;
import solace.script.Engine;
import solace.cmd.GameException;

import java.io.*;
import java.util.*;
import javax.script.ScriptException;

/**
 * Main application class for the Solace engine.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Game
{
  static Server server;
  public static AccountWriter writer;

  /**
   * @return The game server.
   */
  public static Server getServer() {
    return server;
  }

  /**
   * Initializes the game and starts the game server.
   */
  protected static void init(String[] args)
    throws IOException, GameException, ScriptException
  {
    int port;

    try {
      port = Integer.parseInt(args[0]);
    }
    catch (Exception e) {
      port = 4000;
    }

    // Load all configuration files
    Config.load();

    // Load static game messages
    Message.load();

    // Initialize the game world
    World.init();

    // Start the periodic account saver / file writer
    writer = new AccountWriter();
    new Thread(writer).start();

    // Start the main game world clock
    Clock.getInstance().start();

    // Start all management services
    BattleManager.start();
    RecoveryManager.start();
    PlayerManager.start();

    // Initialize the scripting engine
    Engine.start();

    // Initialize and start the game server
    server = new Server(port);
    server.listen();
  }

  /**
   * Safely shuts the game down by saving all characters, stopping all network
   * communication to clients, etc.
   */
  public static void shutdown() {
    Clock.getInstance().stop();
    writer.stop();
    server.shutdown();
  }

  /**
   * Main method for the Solace engine, initializes the game, and starts the
   * game server.
   * @param args
   */
  public static void main(String[] args) {
    try {
      init(args);
    } catch (GameException e) {
      Log.error("Unable to initialize game: " + e.getMessage());
    } catch (IOException e) {
      Log.error("Unable to initialize and start game server:");
      e.printStackTrace();
    } catch (ScriptException e) {
      Log.error("Unable to initialize scripting engine: " + e.getMessage());
    }
  }
}
