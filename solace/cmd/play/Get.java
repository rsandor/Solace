package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * The get command is used by a player to obtain items from rooms,
 * containers, etc. and place the item in the character's inventory.
 * @author Ryan Sandor Richards
 */
public class Get extends PlayCommand {
  public Get(solace.game.Character ch) {
    super("get", ch);
  }

  public boolean run(Connection c, String []params) {
    if (params.length < 2) {
      c.sendln("What would you like to get?\n\r");
      return false;
    }

    if (character.isSleeping()) {
      character.sendln("You cannot get anything while alseep.");
      return false;
    }

    // TODO: Handle "get all" case

    // TODO: Should we handle multiple item pickups at once
    //     in the parameters?

    String name = params[1];
    Room room = character.getRoom();
    Item item = room.findItem(name);

    // If there is no such item, then we're done, inform the player
    if (item == null) {
      c.sendln("You could not find '" + name + "' here.\n\r");
      return false;
    }

    // Remove the item from the room and add it to the character's inventory
    room.removeItem(item);
    character.addItem(item);

    // Inform the users and the rest of the players
    String description = item.get("description.inventory");
    c.sendln("You get " + description);
    room.sendMessage(character.getName() + " gets " + description, character);

    return true;
  }
}
