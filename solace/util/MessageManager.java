package solace.util;

import java.util.*;
import java.io.*;

/**
 * This is a basic message manager for the game. It pulls
 * messages from the "Message/" folder and loads them so
 * that they may be sent to players.
 * 
 * Messages are things like the introduction screen, or any
 * other "static" information that is displayed to users
 * by the game.
 * 
 * @author Ryan Sandor Richards (Gaius)
 */
public class MessageManager 
{
	final String messageDir = "Messages/";
	Hashtable messages = new Hashtable();
	
	/**
	 * Creates a new Message Manager and loads all game messages.
	 * @throws IOException if and i/o error occured while loading messages.
	 */
	public MessageManager()
		throws IOException
	{
		loadMessages();
		Log.info("Game messages loaded");
	}
	
	/**
	 * Loads messages into the manager.
	 */
	void loadMessages()
		throws IOException
	{
		File dir = new File(messageDir);
		File []messageFiles = dir.listFiles();
		for (int i = 0; i < messageFiles.length; i++)
		{
			String name = messageFiles[i].getName();
			StringBuffer contents = new StringBuffer("");
			
			try {
				BufferedReader in = new BufferedReader(new FileReader(messageFiles[i]));
				String line = in.readLine();
				while (line != null)
				{
					contents.append(line	 + "\n");
					line = in.readLine();
				}
				messages.put(name, new String(contents));
			}
			catch (IOException ioe)
			{
				Log.error("Error loading message " + name + " from the messages directory, '" + messageDir + "'.");
			}
		}
	}
		
	/**
	 * Reloads messages on the fly.
	 */
	public void reload()
		throws IOException
	{
		messages.clear();
		loadMessages();
		Log.info("Game Messages Reloaded");
	}
	
	/**
	 * Retrieves a message contents.
	 * @param name Name of the message.
	 */
	public String get(String name)
	{
		return (String)messages.get(name);
	}
}

