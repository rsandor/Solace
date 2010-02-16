package solace.net;

import solace.game.*;
import solace.util.Log;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Main game server, listens for and manages connections to the game.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Server 
{
	private int port;
	private ServerSocket serverSocket;
	private boolean listening = false;
	LinkedList connections = new LinkedList();
	
	/**
	 * Creates a new server for listening on port 4000.
	 * @throws IOException If the server was unable to be initialized on port 4000.
	 */
	public Server()
		throws IOException
	{
		initialize(4000);
	}
	
	/**
	 * Creates a new server for listening on the given port.
	 * @param port Port for the server to listen.
	 * @throws IOException If the server could not be initialized on the given port.
	 */
	public Server(int port)
		throws IOException
	{
		initialize(port);
	}
	
	/**
	 * Initalizes the game server.
	 * @param p Port to listen on.
	 * @throws IOException If the server could not be initialized properly on the given port.
	 */
	protected void initialize(int p)
		throws IOException
	{
		port = p;
		serverSocket = new ServerSocket(port);
		Log.info("Game Server Initialized");
	}
	
	/**
	 * Begins the main listen loop for the server.
	 */
	public void listen()
	{
		listening = true;
		Log.info("Game server running and listening on port " + port);
		
		while (listening)
		{
			try 
			{
				Socket s = serverSocket.accept();
				Log.info("Incoming connection from " + s.getInetAddress().toString());
				initSocket(s);
			}
			catch (IOException ioe)
			{
				if (listening)
					Log.error("Unable to accept connection on port " + port);
			}
		}
	}
	
	/**
	 * Initializes an incoming socket and connects the player to the game.
	 * @param s Socket to initialize.
	 */
	public void initSocket(Socket s)
		throws IOException
	{
		// Create the new connection
		Connection c = new Connection(s);
		
		// Add the connection to the game world
		World.addConnection(c);
		
		// Start the connection thread
		(new Thread(c)).start();
	}
	
	/**
	 * Shuts the server down. This entails dropping all current connections
	 * and ending the listening loop.
	 */
	public void shutdown()
	{
		listening = false;
		
		/*
		 * Close all connections to the server.
		 */
		Collection connections = Collections.synchronizedCollection(World.getConnections());
		synchronized (connections)
		{
			Iterator i = connections.iterator();
			while (i.hasNext())
			{
				Connection c = (Connection)i.next();
				c.sendln("\n{RServer Shutdown!{x\n");
				c.close();
			}
		}
		
		try {
			serverSocket.close();
		}
		catch (IOException ioe)
		{
			Log.error("I/O Error on server socket close during shutdown");
		}
	}
}
