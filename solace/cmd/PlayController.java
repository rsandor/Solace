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
			World.getDefaultRoom().getCharacters().add(ch);
			World.getDefaultRoom().getCharacters().sendMessage(ch.getName() + " has entered the game.");
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
			else if ((new String("enter").startsWith(cmd) || new String("exit").startsWith("exit")) && params.length >= 2) {
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
			Room origin = character.getRoom();
			Room destination = area.getRoom(exit.getToId());
			
			if (destination == null) {
				c.sendln("There is no exit '" + direction + "'");
				Log.error("Null destination encountered on move from '" + 
					character.getRoom().getId() + "' along exit with names '" + 
					exit.getCompiledNames() + "'");
				return;
			}
			
			// Determine the exit and enter messages
			String exitFormat = "%s leaves.";
			String enterFormat = "%s arrives.";
			
			String charName = character.getName();
			if (new String("north").startsWith(direction)) {
				exitFormat = "%s leaves to the north.";
				enterFormat = "%s arrives from the south.";
			}
			else if (new String("south").startsWith(direction)) {
				exitFormat = "%s heads to the south.";
				enterFormat = "%s arrives from the north.";
			}
			else if (new String("east").startsWith(direction)) {
				exitFormat = "%s leaves heading east.";
				enterFormat = "%s arrives from the west.";
			}
			else if (new String("west").startsWith(direction)) {
				exitFormat = "%s heads west.";
				enterFormat = "%s arrives from the east.";
			}
			else if (new String("enter").startsWith(cmd)) {
				exitFormat = "%s enters " + destination.getTitle() + ".";
			}
			else if (new String("exit").startsWith(cmd)) {
				enterFormat = "%s arrives from " + origin.getTitle() + ".";
			}
				
			String cName = character.getName();
				
			// Remove the character from its current room
			origin.getCharacters().remove(character);
			origin.sendMessage(String.format(exitFormat, cName));
			
			// Send it to the destination room
			character.setRoom(destination);
			destination.sendMessage(String.format(enterFormat, cName));
			destination.getCharacters().add(character);
			
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
				
				// Show a list of characters in the room
				c.sendln("{cThe following characters present:{x");
				synchronized(room.getCharacters()) {
					for (solace.game.Character ch : room.getCharacters()) {
						c.sendln(ch.getName() + ".");
					}
				}
				c.sendln("");
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
			Room room = character.getRoom();
			room.getCharacters().remove(character);
			room.sendMessage(String.format("%s has left the game.", character.getName()));
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