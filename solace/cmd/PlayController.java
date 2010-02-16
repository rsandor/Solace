package solace.cmd;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * Main game play controller (the actual game).
 * @author Ryan Sandor Richards
 */
public class PlayController
	extends AbstractStateController
{
	solace.game.Character character;
	
	// Commonly used command instances
	Look look = new Look();
	Move move = new Move();

	/**
	 * Creates a new game play controller.
	 * @param c The connection.
	 * @param ch The character.
	 * @throws GameException if anything goes wrong when logging the user in.
	 */
	public PlayController(Connection c, solace.game.Character ch) 
		throws GameException
	{
		// Initialize the menu
		super(c, "Sorry, that is not an option. Type '{yhelp{x' to see a list.");
		character = ch;
		
		// Character location initialization
		if (ch.getRoom() == null) {	
			ch.setRoom(World.getDefaultRoom());
		}
		
		// Add the main gameplay commands
		addCommand(look);
		
		String []moveAliases = {
			"move", "go", "north", "south", 
			"east", "west", "up", "down", 
			"exit", "enter"
		};
		for (String n : moveAliases)
			addCommand(n, move);
		
		addCommand(new Quit());
		
		World.getActivePlayers().add(c);
		
		c.sendln("\n\rNow playing as {y" + ch.getName() + "{x, welcome!\n\r");
		c.setPrompt("{c>{x ");
		
		look.run(c, new String("look").split(" "));
	}

	/**
	 * The movement command is used to move about the game world.
	 *
	 * Syntax:
	 *   move [direction]
	 *   go [direction]
	 *   north
	 *   south
	 *   east
	 *   west
	 *   up
	 *   down
	 *   enter [place]
	 *   exit [place]
	 */
	class Move extends AbstractCommand {
		public Move() { super("move"); }
		public void run(Connection c, String []params) {
			String cmd = params[0];
			String direction;
			
			if (cmd.equals("move") || cmd.equals("go")) {
				if (params.length < 2) {
					c.sendln("You must provide a direction to move.");
					return;
				}
				direction = params[1];
			} 
			else if ((cmd.equals("enter") || cmd.equals("exit")) && params.length >= 2) {
				direction = params[1];
			}
			else {
				direction = cmd;
			}
		
			Exit exit = character.getRoom().findExit(direction);
			if (exit == null) {
				c.sendln("There is no exit '" + direction + "'");
				return;
			}
			
			Area area = character.getRoom().getArea();

			Room destination = area.getRoom(exit.getToId());
			if (destination == null) {
				Log.error("Null destination encountered on move from '" + 
					character.getRoom().getId() + "' along exit with names '" + 
					exit.getCompiledNames() + "'");
				return;
			}
			
			character.setRoom(destination);
			
			// TODO Send movement messages to everyone in the room
			
			look.run(c, new String("look").split(" "));
		}
	}
	
	/**
	 * The look command is used to examine rooms, characters, and objects in the game world.
	 * 
	 * Syntax: 
	 *   look [player name | item | etc..]
	 *   examine [item]
	 *  
	 * @author Ryan Sandor Richards
	 */
	class Look extends AbstractCommand {
		public Look() { super("look"); }
		public void run(Connection c, String []params) {
			// TODO Implement examine and looking at characters and items
			if (params.length == 1) {
				Room room = character.getRoom();
				c.sendln("{y" + room.getTitle().trim() + "{x\n");
				c.sendln(Strings.toFixedWidth(room.getDescription(), 80).trim() + "\n");
			}
			else {
				String id = params[2];
				c.sendln("You do not see '{r" + id + "{x' here.");
			}
		}
	}
	
	/**
	 * Quits the game and returns to the main menu.
	 * @author Ryan Sandor Richards.
	 */
	class Quit extends AbstractCommand {
		public Quit() { super("quit"); }
		public void run(Connection c, String []params) {
			World.getActivePlayers().remove(c);
			c.setStateController( new MainMenu(c) );
		}
	}
	
	/**
	 * Help Command
	 * @author Ryan Sandor Richards (Gaius)
	 */
	class Help extends AbstractCommand {
		public Help() { super("help"); }
		public void run(Connection c, String []params) {
		}
	}
}