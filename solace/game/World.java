package solace.game;

import java.util.*;
import solace.util.Log;
import solace.net.Connection;

/**
 * Holds the state of the entire game world.
 * @author Ryan Sandor Richards (Gaius)
 */
public class World 
{
	List<Connection> connections;
	List<Connection> oogChat;
	Hashtable<String, Account> namesToAccounts;
	Hashtable<Account, Connection> accountsToConnections;
	
	/**
	 * Creates a new game world.
	 */
	public World()
	{
		Log.info("Game world loaded");
		connections = Collections.synchronizedList(new LinkedList<Connection>());
		oogChat = Collections.synchronizedList(new LinkedList<Connection>());
		namesToAccounts = new Hashtable<String, Account>();
		accountsToConnections = new Hashtable<Account, Connection>();
	}
	
	/**
	 * Determines if an account is currently logged in.
	 * @param name Name of the account to check for.
	 * @return True if an account with the given name is logged in.
	 */
	public synchronized boolean isLoggedIn(String name)
	{
		return namesToAccounts.containsKey(name.toLowerCase());
	}
	
	/**
	 * Adds an account to the game world.
	 * @param a Account to add
	 */
	public synchronized void addAccount(Connection c, Account a)
	{
		namesToAccounts.put(a.getName(), a);
		accountsToConnections.put(a, c);
	}
	
	/**
	 * Finds a connection, given an account.
	 * @param a Account to determine the connection for.
	 * @return The connection associated with the account, returns null if no connection was found.
	 */
	public synchronized Connection connectionFromAccount(Account a)
	{
		return (Connection)accountsToConnections.get(a);
	}
	
	/**
	 * Finds a connection, given an account name.
	 * @param name Name of the account to find the connection for.
	 * @return The connection associated with the account of the given name, returns null if no connection was found.
	 */
	public synchronized Connection connectionFromName(String name)
	{
		return connectionFromAccount((Account)namesToAccounts.get(name));
	}
	
	/**
	 * Logs an account out of game world.
	 * @param a Account to remove.
	 */
	public synchronized void removeAccount(Account a)
	{
		namesToAccounts.remove(a.getName().toLowerCase());
		accountsToConnections.remove(a);
	}
	
	/**
	 * Adds a connection to the game world.
	 * @param c Connection to add.
	 */
	public synchronized void addConnection(Connection c)
	{
		connections.add(c);
	}

	/**
	 * Removes a connection from the game world.
	 * @param c Connection to remove.
	 */
	public synchronized void removeConnection(Connection c)
	{
		connections.remove(c);
	}
	
	/**
	 * @return the connections
	 */
	public Collection<Connection> getConnections() 
	{
		return connections;
	}

	/**
	 * @return the oogChat
	 */
	public Collection<Connection> getOogChat() 
	{
		return oogChat;
	}

}


