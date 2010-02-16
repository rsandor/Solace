package solace.game;

import solace.net.*;
import solace.util.*;
import solace.xml.*;
import solace.cmd.GameException;

import java.io.*;
import java.util.*;

/**
 * Main application class for the Solace engine.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Game 
{
	static Server server;
	
	/**
	 * @return The game server.
	 */
	public static Server getServer() {
		return server;
	}

	/**
	 * Initializes the game and starts the game server.
	 */
	protected static void init(String[] args) throws IOException, GameException {
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

		// Initialize and start the game server
		server = new Server(port);
		server.listen();
	}

	/**
	 * Main method for the Solace engine, initializes the game, and starts the game server.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try {
			init(args);
		}
		catch (GameException e) {
			Log.error("Unable to initialize game: " + e.getMessage());
		}
		catch (IOException e) {
			Log.error("Unable to initialize and start game server:");
			e.printStackTrace();
		}
	}
}
