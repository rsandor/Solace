package solace.game;
import solace.cmd.CommandRegistry;
import solace.io.*;
import solace.net.*;
import solace.util.*;
import solace.script.ScriptingEngine;
import solace.cmd.GameException;
import java.io.*;
import java.util.*;
import javax.script.ScriptException;

/**
 * Main application class for the Solace engine.
 * @author Ryan Sandor Richards
 */
public class Game {
  private static Server server;
  public static final AccountWriter writer = new AccountWriter();
  private static final List<Connection> connections = Collections.synchronizedList(new LinkedList<Connection>());
  private static final List<Connection> oogChat = Collections.synchronizedList(new LinkedList<Connection>());
  private static final Hashtable<String, Account> namesToAccounts =  new Hashtable<>();
  private static final Hashtable<Account, Connection> accountsToConnections = new Hashtable<>();
  private static final List<solace.game.Character> playing = Collections.synchronizedList(
    new LinkedList<solace.game.Character>());

  /**
   * Initializes the game and starts the game server.
   */
  private static void start(String[] args)
    throws IOException, GameException, ScriptException
  {
    Config.load();
    Messages.reload();
    Dreams.getInstance().reload();
    HelpSystem.getInstance().reload();
    DamageTypes.getInstance().reload();
    WeaponProficiencies.getInstance().reload();
    Skills.getInstance().reload();
    Races.getInstance().reload();
    Buffs.initialize();
    Areas.getInstance().reload();

    new Thread(writer).start();

    Clock.getInstance().start();

    BattleManager.start();
    RecoveryManager.start();
    PlayerManager.start();
    DreamManager.start();

    ScriptingEngine.reload();
    CommandRegistry.reload();

    Log.info("Game engine loaded and running");

    int port;
    try {
      port = Integer.parseInt(args[0]);
    } catch (Throwable t) {
      port = 4000;
    }
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
   * Determines if an account is currently logged in.
   * @param name Name of the account to check for.
   * @return True if an account with the given name is logged in.
   */
  public static synchronized boolean isLoggedIn(String name) {
    return namesToAccounts.containsKey(name.toLowerCase());
  }

  /**
   * Adds an account to the game world.
   * @param a Account to add
   */
  public static synchronized void addAccount(Connection c, Account a) {
    namesToAccounts.put(a.getName(), a);
    accountsToConnections.put(a, c);
  }

  /**
   * Finds a connection, given an account.
   * @param a Account to determine the connection for.
   * @return The connection associated with the account, returns null if no
   *   connection was found.
   */
  static synchronized Connection connectionFromAccount(Account a) {
    if (!accountsToConnections.containsKey(a))
      return null;
    return accountsToConnections.get(a);
  }

  /**
   * Logs an account out of game world.
   * @param a Account to remove.
   */
  public static synchronized void removeAccount(Account a) {
    namesToAccounts.remove(a.getName().toLowerCase());
    accountsToConnections.remove(a);
  }

  /**
   * Adds a connection to the game world.
   * @param c Connection to add.
   */
  public static synchronized void addConnection(Connection c)
  {
    connections.add(c);
  }

  /**
   * Removes a connection from the game world.
   * @param c Connection to remove.
   */
  public static synchronized void removeConnection(Connection c)
  {
    connections.remove(c);
  }

  /**
   * @return All the connections currently playing a character in the game
   *   world.
   */
  public static Collection<Character> getActiveCharacters() {
    return playing;
  }

  /**
   * @return An unmodifiable collection of all player players and mobiles
   *   in the game world.
   */
  static Collection<Player> getAllPlayers() {
    List<Player> allPlayers = new LinkedList<>();
    synchronized(playing) {
      allPlayers.addAll(playing);
    }
    allPlayers.addAll(MobileManager.getInstance().getMobiles());
    return Collections.unmodifiableCollection(allPlayers);
  }

  /**
   * @return the connections
   */
  public static Collection<Connection> getConnections() {
    return connections;
  }

  /**
   * @return An unmodifiable list of chat connections.
   */
  public static Collection<Connection> getChatConnections() {
    synchronized (oogChat) {
      return Collections.unmodifiableCollection(oogChat);
    }
  }

  /**
   * Adds a connection to the out of game chat.
   * @param c Connection to add.
   */
  public static void addChatConnection(Connection c) {
    oogChat.add(c);
  }

  /**
   * Removes a connection for out of game chat.
   * @param c The connection to remove.
   */
  public static void removeChatconnection(Connection c) {
    oogChat.remove(c);
  }

  /**
   * Main method for the Solace engine, initializes the game, and starts the game server.
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    try {
      start(args);
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
