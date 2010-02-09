package solace.game;

import solace.net.*;
import solace.util.*;
import java.io.*;

/**
 * Main application class for the Solace engine.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Game 
{
	static Server server;
	static MessageManager messageManager;
	static World world;
	
	/**
	 * @return The game world.
	 */
	public static World getWorld()
	{
		return world;
	}
	
	/**
	 * @return The game's message manager.
	 */
	public static MessageManager getMessageManager()
	{
		return messageManager;
	}
	
	/**
	 * @return The game server.
	 */
	public static Server getServer()
	{
		return server;
	}

	/**
	 * Main method for the Solace engine, initializes the game, and starts the game server.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try 
		{
			// Initialize the message manager
			messageManager = new MessageManager();
			
			// Initialize the game world
			world = new World();

			// Initialize and start the game server
			server = new Server();
			server.listen();
		}
		catch (IOException ioe)
		{
			Log.error("Unable to initialize and start game server!");
		}
	}
}
