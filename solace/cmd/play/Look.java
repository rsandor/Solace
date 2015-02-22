package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * The look command is used to examine rooms, characters, and objects in the game world.
 *
 * Syntax:
 *   look [player name | item | etc..]
 *   examine [item]
 *
 * @author Ryan Sandor Richards
 */
public class Look extends PlayCommand {
    public Look(solace.game.Character ch) {
        super("look", ch);
    }

    public boolean run(Connection c, String []params) {
        if (params.length == 1) {
            // Describe the room to the character
            Room room = character.getRoom();
            c.sendln(room.describeTo(character));

            // Inform other players that the character is looking around
            room.sendMessage(character.getName() + " looks around.", character);
        }
        else {
            // TODO Implement examine and looking at characters and items
            String id = params[2];
            c.sendln("You do not see '{r" + id + "{x' here.");
        }

        return true;
    }
}
