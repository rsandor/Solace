package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * The look command is used to examine rooms, characters, and objects in the
 * game world.
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
      return true;
    }

    String name = params[1];

    // Check for room features
    String feature = room.describeFeature(name);
    if (feature != null) {
      c.sendln(feature);
      return true;
    }

    // Check for items
    String item = room.describeItem(name);
    if (item != null) {
      c.sendln(item);
      return true;
    }

    // Default "You don't see that" message
    c.sendln("You do not see '{g" + name + "{x' here.");
    return true;
  }
}
