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

    static final String[] moveAliases = {
        "move", "go", "north", "south",
        "east", "west", "up", "down",
        "exit", "enter"
    };

    // Commonly used command instances
    Look look = new Look();
    Move move = new Move();
    Say say = new Say();
    Help help = new Help();
    Scan scan = new Scan();

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

        // Place the player in the world and force a look command
        World.getActivePlayers().add(c);
        c.sendln("\n\rNow playing as {y" + ch.getName() + "{x, welcome!\n\r");
        c.setPrompt("{c>{x ");

        look.run(c, new String("look").split(" "));
    }

    /**
     * Adds basic gameplay commands to the controller.
     */
    protected void addCommands() {
        addCommand(look);
        for (String n : moveAliases)
            addCommand(n, move);
        addCommand(new Quit());
        addCommand(say);
        addCommand(help);
        addCommand(scan);
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
            boolean notEast = !(new String("east").startsWith(cmd));

            if (cmd.equals("move") || cmd.equals("go")) {
                if (params.length < 2) {
                    c.sendln("What direction would you like to move?");
                    return;
                }
                direction = params[1];
            }
            else if (new String("enter").startsWith(cmd) && notEast) {
                if (params.length < 2) {
                    c.sendln("Where would you like to enter?");
                    return;
                }
                direction = params[1];
            }
            else if (new String("exit").startsWith(cmd) && notEast) {
                if (params.length < 2) {
                    c.sendln("Where would you like to exit?");
                    return;
                }
                direction = params[1];
            }
            else {
                direction = cmd;
            }

            if (direction == null) {
                c.sendln("That is not a direction.");
                Log.error("Null direction encountered during move.");
                return;
            }

            Exit exit = character.getRoom().findExit(direction);
            if (exit == null) {
                c.sendln("There is no exit '" + direction + "'.");
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
            else if (new String("up").startsWith(direction)) {
                exitFormat = "%s leaves heading up.";
                enterFormat = "%s arrives from below.";
            }
            else if (new String("down").startsWith(direction)) {
                exitFormat = "%s leaves going down.";
                enterFormat = "%s arrives from above.";
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
        public Look() {super("look"); }
        public void run(Connection c, String []params) {
            // TODO Implement examine and looking at characters and items
            if (params.length == 1) {
                Room room = character.getRoom();
                c.sendln("{y" + room.getTitle().trim() + "{x\n");
                c.sendln(Strings.toFixedWidth(room.getDescription(), 80).trim() + "\n");

                // Show a list of characters in the room
                List<solace.game.Character> others = room.getOtherCharacters(character);
                if (others.size() > 0) {
                    c.sendln("{cThe following characters are present:{x");
                    for (solace.game.Character ch : others) {
                        if (ch == character)
                            continue;
                        c.sendln(ch.getName() + ".");
                    }
                }
                else {
                    c.sendln("{cYou are the only one here.{x");
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
     * Help Command. Currently only displays a list of commands available to the user.
     * @author Ryan Sandor Richards
     */
    class Help extends AbstractCommand {
        public Help() { super("help"); }
        public void run(Connection c, String []params) {
            String commandText = "\n{cAvailable Commands:{x\n";
            for (CommandTuple cmd : commands) {
                commandText += cmd.getName() + " ";
            }
            c.sendln(commandText + "\n");
        }
    }

    /**
     * The say command, allows players to speak to eachother in a given room.
     * @author Ryan Sandor Richards
     */
    class Say extends AbstractCommand {
        public Say() {
            super("say");
        }

        public void run(Connection c, String []params) {
            if (params.length < 2) {
                c.sendln("What would you like to say?");
                return;
            }

            // Format the message
            String message = "'";
            for (int i = 1; i < params.length; i++) {
                message += params[i];
                if (i != params.length - 1)
                    message += " ";
            }
            message += "'\n";

            // Broadcast to the room
            Room room = character.getRoom();
            synchronized(room.getCharacters()) {
                for (solace.game.Character ch : room.getCharacters()) {
                    if (ch == character)
                        c.sendln("You say " + message);
                    else
                        ch.sendMessage(character.getName() + " says " + message);
                }
            }
        }
    }

    /**
     * Scan command, shows players and mobiles in adjacent rooms.
     * @author Ryan Sandor Richards
     */
    class Scan extends AbstractCommand {
        public Scan() {
            super("scan");
        }

        public void run(Connection c, String []params) {
            Room room = character.getRoom();
            Area area = room.getArea();
            List<Exit> exits = room.getExits();

            String message = "";

            for (Exit ex : exits) {
                Room r = area.getRoom(ex.getToId());

                if (r.getCharacters().size() == 0)
                    continue;

                message += ex.getDescription().trim() + ":\n\r";

                synchronized(r.getCharacters()) {
                    for (solace.game.Character ch : r.getCharacters()) {
                        message += "    " + ch.getName() + "\n\r";
                    }
                }
            }

            if (message == "") {
                message = "There is nobody of interest in any direction.";
            }

            c.sendln(message);
        }
    }
}