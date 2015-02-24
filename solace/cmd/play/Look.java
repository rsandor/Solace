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
        Room room = character.getRoom();

        if (params.length == 1) {
            c.sendln(room.describeTo(character));
            room.sendMessage(character.getName() + " looks around.", character);
        }
        else {
            String name = params[1],
                feature = room.describeFeature(name);

            if (feature != null)
                c.sendln(feature);
            else
                c.sendln("You do not see '{g" + name + "{x' here.");
        }

        return true;
    }
}
