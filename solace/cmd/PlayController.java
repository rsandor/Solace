package solace.cmd;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;
import solace.cmd.play.*;

/**
 * Main game play controller (the actual game).
 * @author Ryan Sandor Richards
 */
public class PlayController
    extends AbstractStateController
{
    solace.game.Character character;

    static final String[] moveAliases = {
        "move", "go", "north", "south",
        "east", "west", "up", "down",
        "exit", "enter"
    };

    /**
     * Quit command. Exits the game and returns to the main menu.
     * @author Ryan Sandor Richards.
     */
    class Quit extends AbstractCommand {
        public Quit() {
            super("quit");
        }

        public boolean run(Connection c, String []params) {
            Room room = character.getRoom();
            room.getCharacters().remove(character);
            room.sendMessage(String.format("%s has left the game.", character.getName()));
            World.getActivePlayers().remove(c);
            c.setStateController( new MainMenu(c) );
            return true;
        }
    }

    /**
     * Help Command. Currently only displays a list of commands available to the user.
     * @author Ryan Sandor Richards
     */
    class Help extends AbstractCommand {
        public Help() {
            super("help");
        }

        public boolean run(Connection c, String []params) {
            String commandText = "\n{cAvailable Commands:{x\n";
            for (CommandTuple cmd : commands) {
                commandText += cmd.getName() + " ";
            }
            c.sendln(commandText + "\n");
            return true;
        }
    }

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
            Room room = World.getDefaultRoom();
            room.getCharacters().add(ch);
            ch.setRoom(room);
        }

        // Inform other players in the room that they player has entered the game
        ch.getRoom().sendMessage(character.getName() + " has entered the game.", character);

        // Add commands
        addCommands();

        // Place the player in the world
        World.getActivePlayers().add(c);
        c.sendln("\n\rNow playing as {y" + ch.getName() + "{x, welcome!\n\r");
        c.setPrompt("{c>{x ");

        // Describe the room to the player
        c.sendln(ch.getRoom().describeTo(ch));
    }

    /**
     * Adds basic gameplay commands to the controller.
     */
    protected void addCommands() {
        addCommand(new Quit());
        addCommand(new Help());
        addCommand(new Look(character));
        addCommand(new Say(character));
        addCommand(new Scan(character));
        addCommand(moveAliases, new Move(character));
    }
}
