package solace.cmd;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.Log;

/**
 * Controller for the game's main menu.
 * @author Ryan Sandor Richards (Gaius)
 */
public class MainMenu
	extends AbstractStateController
{
	/**
	 * Creates a new main menu controller.
	 */
	public MainMenu(Connection c)
	{
		// Initialize the menu
		super(c, "Sorry, that is not an option. Type '{yhelp{x' to see a list.");
		
		// Set the user's prompt and welcome them
		c.setPrompt("{cChoose an option:{x ");
		
		// Neato trick, actually use the help command to show the menu on login:
		Command help = new Help();
		help.run(c, new String("help").split(" "));
		
		// Add all of the commands to the main menu
		addCommand(help);
		addCommand(new Quit());
		addCommand(new Who());
		addCommand(new Chat());
		
		addCommand(new Shutdown());
		addCommand(new Reload());
		addCommand(new Peek());
		
	}
	
	/**
	 * Chat command - Logs people into the out of game (OOG) chat room.
	 * @author Ryan Sandor Richards
	 */
	class Chat extends AbstractCommand
	{
		public Chat() { super("Chat"); }
		public void run(Connection c, String []params)
		{
			c.setStateController(new ChatController(c));
		}
	}
	
	/**
	 * Help Command
	 * @author Ryan Sandor Richards (Gaius)
	 */
	class Help extends AbstractCommand
	{
		public Help() { super("help"); }
		public void run(Connection c, String []params)
		{
			c.sendln(Game.getMessageManager().get("MainMenu"));
			if (c.getAccount().getAccountType() == Account.AT_ADMIN)
				c.sendln( Game.getMessageManager().get("AdminMenu") );			
		}
	}
	
	/**
	 * Quit Command
	 * @author Ryan Sandor Richards (Gaius)
	 */
	class Quit extends AbstractCommand
	{
		public Quit() { super("quit"); }
		public synchronized void run(Connection c, String []params)
		{
			c.sendln("Goodbye!");
			
			// If the connection has an account, remove it from the accounts list
			if (connection.hasAccount())
				Game.getWorld().removeAccount(connection.getAccount());
			
			// Remove it from the connections list
			Game.getWorld().removeConnection(connection);
			
			c.close();
		}
	}
	
	/**
	 * Who command - lists who is online.
	 * @author Ryan Sandor Richards (Gaius)
	 */
	class Who extends AbstractCommand
	{
		public Who() { super("who"); }
		
		/*
		 * Note: This whole thing will become SIGNIFICANTLY easier
		 * when I switch the code base to java 6.
		 */
		public void run(Connection c, String []params)
		{
			Collection connections = Game.getWorld().getConnections();		
			c.sendln("{y---- {xPlayers Online{y ----{x\n");
		
			synchronized (connections)
			{
				Iterator iter = connections.iterator(); 
				while (iter.hasNext())
				{
					Connection oc = (Connection)iter.next();
					if (oc.hasAccount())
					{
						Account acct = (Account)oc.getAccount();
						c.sendln(acct.getName());
					}
				}
			}
		}
	}
	
	
	/**
	 * Shutdown (Admin Command) - Safely shuts the game server down and exits the program.
	 * TODO Make this more robust, for now it will work fine. What I want to do is have
	 * it take a couple of arguments (message, possibly a time in minutes for the shutdown
	 * to occur, for instance).
	 * 
	 * @author Ryan Sandor Richards (Gaius)
	 */
	class Shutdown extends AdminCommand
	{
		public Shutdown() { super("shutdown"); }
		public void run(Connection c, String []params)
		{
			Game.getServer().shutdown();
		}
	}
	
	/**
	 * Reload (Admin Command) - Reloads all "static" game messages (such as help files, etc.).
	 * This is useful for when you have to make changes/corrections to game message files and
	 * you just need to quickly update them without doing a full reboot/reload of the game.
	 * 
	 * TODO Eventually I would like the 'reload' command to allow admins to reload more than
	 * just game messages. They should be able to specify what they want to reload via an
	 * argument to the command and it will reload the specific thing they wish (areas, messages,
	 * etc.)
	 * 
	 * @author Ryan Sandor Richards (Gaius)
	 */
	class Reload extends AdminCommand
	{
		public Reload() { super("reload"); }
		public void run(Connection c, String []params)
		{
			try
			{
				Game.getMessageManager().reload();
				c.sendln("Game messages reloaded.");
			}
			catch (IOException ioe)
			{
				c.sendln("An error occured while trying to reload game messages!");
				Log.error("An IO exception occured when reloading messages (reload spawned by user '" +
						c.getAccount().getName().toLowerCase() + "')");
			}			
		}
	}
	
	/**
	 * The peek command allows an administrator to view details about a particular player's
	 * connection to the game.
	 * 
	 * Note: This will get more and more detailed as the engine gets fleshed out.
	 * 
	 * @author Ryan Sandor Richards
	 */
	class Peek extends AdminCommand
	{
		public Peek() { super("peek"); }
		
		public void run(Connection c, String []params)
		{
			if (params.length < 2)
			{
				c.sendln("Syntax: peek <player1> <player2> ... | all");
				return;
			}
			
			World world = Game.getWorld();
			
			if (params[1].toLowerCase().equals("all"))
			{
				Collection connections = Collections.synchronizedCollection(world.getConnections());
				synchronized (connections)
				{
					Iterator i = connections.iterator();
					while (i.hasNext())
						c.sendln( formatInfo((Connection)i.next()) );
				}
			}
			else
			{
				// Generate peek reports for each of the given names
				for (int i = 1; i < params.length; i++)
				{
					if (!world.isLoggedIn(params[i]))
						c.sendln("Player '"+params[i]+"' is not currently logged in.");
					else
					{
						Connection target = world.connectionFromName(params[i]);
						c.sendln(formatInfo(target));
					}
				}
			}
		}
		
		/**
		 * Helper function to format information for the peek command.
		 * @param c Connection to peek into.
		 * @return Formatted peek information about the user.
		 */
		protected String formatInfo(Connection c)
		{
			String format = "Player '" + c.getAccount().getName() + "', logged on at: " + c.getConnectionTime() +
			" from address: " + c.getInetAddress();
			return format;
		}
	}
	
	
	
} // End of MainMenu
