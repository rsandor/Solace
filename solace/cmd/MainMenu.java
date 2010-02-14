package solace.cmd;

import java.util.*;
import java.io.*;

import solace.game.*;
import solace.net.*;
import solace.util.*;


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
		addCommand(new List());
		addCommand(new Create());
		addCommand(new Play());
		
		addCommand(new Shutdown());
		addCommand(new Reload());
		addCommand(new Peek());
		
	}
	
	/**
	 * List command - lists all the characters for the user's account.
	 * @author Ryan Sandor Richards.
	 */
	class List extends AbstractCommand {
		public List() { super("list"); }
		public void run(Connection c, String []params) {
			Collection<solace.game.Character> chars = c.getAccount().getCharacters();
			if (chars.size() == 0) {
				c.sendln("You have no characters, use the '{ycreate{x' command to create a new one.");
			}
			else {
				c.sendln("{y---- {xYour Characters {y----{x");
				for (solace.game.Character ch : chars) {
					c.sendln(ch.getName());
				}
				c.sendln("");
			}
		}
	}
	
	/**
	 * Create command - allows players to create new characters.
	 * @author Ryan Sandor Richards
	 */
	class Create extends AbstractCommand {
		public Create() { super("create"); }
		public void run(Connection c, String []params) {
			c.setStateController( new CreateCharacter(c) );
		}
	}
	
	/**
	 * Play - enter and play the main game with a character.
	 */
	class Play extends AbstractCommand {
		public Play() { super("play"); }
		public void run(Connection c, String []params) {
			try {
				if (params.length < 2) {
					c.sendln("You must choose a character to play, use the '{ylist{x' command to see a list of your characters.");
					return;
				}
			
				String name = params[1];
				Account act = c.getAccount();
				if (!act.hasCharacter(name)) {
					c.sendln("Character '" + name + "' not found, use the '{ylist{x' command to see a list of your characters.");
					return;
				}
		
				c.setStateController(new PlayController(c, act.getCharacter(name)));
			}
			catch (GameException ge) {
				Log.error(ge.getMessage());
				c.sendln("An {rerror{x occured, please try again later.");
			}
		}
	}
	
	/**
	 * Chat command - Logs people into the out of game (OOG) chat room.
	 * @author Ryan Sandor Richards
	 */
	class Chat extends AbstractCommand
	{
		public Chat() { super("chat"); }
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
			c.sendln(Message.get("MainMenu"));
			if (c.getAccount().isAdmin())
				c.sendln(Message.get("AdminMenu"));			
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
	 * @author Ryan Sandor Richards
	 */
	class Who extends AbstractCommand
	{
		public Who() { super("who"); }
		
		/*
		 * Note: This whole thing will become SIGNIFICANTLY easier
		 * when I switch the code base to java 6.
		 */
		public void run(Connection c, String []params) {
			Collection connections = Game.getWorld().getConnections();		
			c.sendln("{y---- {xPlayers Online{y ----{x");
			synchronized (connections) {
				Iterator iter = connections.iterator(); 
				while (iter.hasNext()) {
					Connection oc = (Connection)iter.next();
					if (oc.hasAccount()) {
						Account acct = (Account)oc.getAccount();
						c.sendln(acct.getName());
					}
				}
			}
			c.sendln("");
		}
	}
	
	
	/**
	 * Shutdown (Admin Command) - Safely shuts the game server down and exits the program.
	 * TODO Make this more robust, for now it will work fine. What I want to do is have
	 * it take a couple of arguments (message, possibly a time in minutes for the shutdown
	 * to occur, for instance).
	 * 
	 * @author Ryan Sandor Richards
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
	 * @author Ryan Sandor Richards
	 */
	class Reload extends AdminCommand
	{
		public Reload() { super("reload"); }
		
		public void run(Connection c, String []params) {
			try
			{
				Message.reload();
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
