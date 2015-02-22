package solace.game;

import java.io.*;
import java.util.*;

import solace.util.*;
import solace.net.Connection;
import solace.xml.GameParser;
import solace.cmd.GameException;


/**
 * Holds the state of the entire game world.
 * @author Ryan Sandor Richards (Gaius)
 */
public class World
{
	static List<Connection> connections;
	static List<Connection> oogChat;
	static Hashtable<String, Account> namesToAccounts;
	static Hashtable<Account, Connection> accountsToConnections;
	static Hashtable<String, Area> areas;

	static List<solace.game.Character> playing;

	static final String areaDir = "data/areas/";

	static boolean initialized = false;
	static boolean areasLoaded = false;

	/**
	 * Initializes the game world: loads areas and sets up collections.
	 * @throws GameException If the default room could not be found after areas are loaded.
	 */
	public static void init() throws GameException
	{
		if (initialized)
			return;

		loadAreas();

		connections = Collections.synchronizedList(new LinkedList<Connection>());
		oogChat = Collections.synchronizedList(new LinkedList<Connection>());
		playing = Collections.synchronizedList(new LinkedList<solace.game.Character>());
		namesToAccounts = new Hashtable<String, Account>();
		accountsToConnections = new Hashtable<Account, Connection>();

		Log.info("Game world loaded");

		initialized = true;
	}

	/**
	 * Attempts to loads all game areas.
	 * @throws GameException if no default room could be determined after the load.
	 */
	public static synchronized void loadAreas() throws GameException {
		Hashtable<String, Area> newAreas = new Hashtable<String, Area>();

		File dir = new File(areaDir);
		String[] names = dir.list();

		if (names != null) {
			// Try to load all the areas...
			for (int i = 0; i < names.length; i++) {
				try {
				  String fileName = areaDir + names[i];
					Area a = GameParser.parseArea(fileName);
					newAreas.put(a.getId(), a);
					Log.info("Area '" + a.getId() + "' successfully loaded from '" + names[i] + "'");
				}
				catch (IOException ioe) {
					Log.error("Area '" + names[i] + "' failed to load.");
				}
			}

			// Attempt to find the default area
			findDefaultRoom(newAreas);
		}
		else {
			Log.info("No area files available to load.");
		}

		areas = newAreas;
	}

	/**
	 * @param id An area id.
	 * @return The <code>Area</code> associated with the id, or null if none exists.
	 */
	public static Area getArea(String id) {
		if (!areas.containsKey(id))
			return null;
		return areas.get(id);
	}

	/**
	 * Determines if an account is currently logged in.
	 * @param name Name of the account to check for.
	 * @return True if an account with the given name is logged in.
	 */
	public static synchronized boolean isLoggedIn(String name)
	{
		return namesToAccounts.containsKey(name.toLowerCase());
	}

	/**
	 * Adds an account to the game world.
	 * @param a Account to add
	 */
	public static synchronized void addAccount(Connection c, Account a)
	{
		namesToAccounts.put(a.getName(), a);
		accountsToConnections.put(a, c);
	}

	/**
	 * Finds a connection, given an account.
	 * @param a Account to determine the connection for.
	 * @return The connection associated with the account, returns null if no connection was found.
	 */
	public static synchronized Connection connectionFromAccount(Account a)
	{
		if (!accountsToConnections.containsKey(a))
			return null;
		return accountsToConnections.get(a);
	}

	/**
	 * Finds a connection, given an account name.
	 * @param name Name of the account to find the connection for.
	 * @return The connection associated with the account of the given name,
	 *  returns null if no connection was found.
	 */
	public static synchronized Connection connectionFromName(String name)
	{
		return connectionFromAccount((Account)namesToAccounts.get(name));
	}

	/**
	 * Logs an account out of game world.
	 * @param a Account to remove.
	 */
	public static synchronized void removeAccount(Account a)
	{
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
	 * @return All the connections currently playing a character in the game world.
	 */
	public static Collection<solace.game.Character> getActiveCharacters() {
		return playing;
	}

	/**
	 * @return the connections
	 */
	public static Collection<Connection> getConnections()
	{
		return connections;
	}

	/**
	 * @return the oogChat
	 */
	public static Collection<Connection> getChatConnections()
	{
		return oogChat;
	}


	/**
	 * Finds the default room in a list of areas.
	 * @param areaHash Hashtable to search for the default area.
	 * @return The default room in the area hash.
	 */
	protected static Room findDefaultRoom(Hashtable<String, Area> areaHash) throws GameException {
		String aName = Config.get("world.default.area");
		if (aName == null)
			throw new GameException("Required configuration key 'world.default.area' does not exist.");

		String rName = Config.get("world.default.room");
		if (rName == null)
			throw new GameException("Required configuration key 'world.default.room' does not exist.");

		if (!areaHash.containsKey(aName))
			throw new GameException("Default area with id '" + aName + "' does not exist.");

		Area area = areaHash.get(aName);

		Room room = area.getRoom(rName);
		if (room == null)
			throw new GameException("Default room with id '" + rName + "' does not exist.");

		return room;
	}

	/**
	 * @return The default room for the game world.
	 * @throws GameException if the configuration does not have the appropriate keys to fetch the default room.
	 */
	public static Room getDefaultRoom() throws GameException {
		return findDefaultRoom(areas);
	}
}


