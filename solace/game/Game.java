package solace.game;
import solace.cmd.CommandRegistry;
import solace.net.*;
import solace.util.*;
import solace.script.ScriptingEngine;
import solace.cmd.GameException;
import java.io.*;
import javax.script.ScriptException;

/**
 * Main application class for the Solace engine.
 * @author Ryan Sandor Richards
 */
public class Game {
  private static Server server;
  public static AccountWriter writer;

  /**
   * Initializes the game and starts the game server.
   */
  private static void init(String[] args)
    throws IOException, GameException, ScriptException
  {
    int port;
    try {
      port = Integer.parseInt(args[0]);
    } catch (Throwable t) {
      port = 4000;
    }

    Config.load();
    Message.load();
    World.init();

    writer = new AccountWriter();
    new Thread(writer).start();

    Clock.getInstance().start();

    BattleManager.start();
    RecoveryManager.start();
    PlayerManager.start();

    ScriptingEngine.start();
    CommandRegistry.reload();

    server = new Server(port);
    server.listen();
  }

  /**
   * Safely shuts the game down by saving all players, stopping all network
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
   * @param args Command line arguments.
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
