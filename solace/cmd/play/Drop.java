package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * The drop command is used to remove items from a character's inventory
 * and place them into the current room.
 * @author Ryan Sandor Richards
 */
public class Drop extends PlayCommand {
  public Drop(solace.game.Character ch) {
    super("drop", ch);
  }

  public boolean run(Connection c, String []params) {
    if (params.length == 1) {
      c.sendln("What would you like to drop?");
      return false;
    }

    if (character.isSleeping()) {
      c.sendln("You cannot drop anything, for you are fast asleep.");
      return false;
    }

    String name = params[1];
    Item item = character.getItem(name);

    if (item == null) {
      c.sendln("You do not currently possess '" + name + "'.");
      return false;
    }

    String description = item.get("description.inventory");
    Room room = character.getRoom();
    character.removeItem(item);
    room.addItem(item);

    c.sendln("You drop " + description);
    room.sendMessage(
      character.getName() + " drops " + description + "\n\r", character
    );

    return true;
  }
}
