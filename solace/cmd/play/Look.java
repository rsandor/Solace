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
            Room room = character.getRoom();
            List<solace.game.Character> others = room.getOtherCharacters(character);

            c.sendln("{y" + room.getTitle().trim() + "{x\n");
            c.sendln(Strings.toFixedWidth(room.getDescription(), 80).trim() + "\n");

            // Show a list of characters in the room
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
            // TODO Implement examine and looking at characters and items
            String id = params[2];
            c.sendln("You do not see '{r" + id + "{x' here.");
        }

        return true;
    }
}
