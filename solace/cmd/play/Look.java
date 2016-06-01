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

    if (character.isSleeping()) {
      character.sendln("You cannot see anything, for you are alseep.");
      return false;
    }

    if (params.length == 1) {
      c.sendln(room.describeTo(character));
      room.sendMessage(character.getName() + " looks around.", character);
      return true;
    }

    String name = params[1];

    String featureDesc = room.describeFeature(name);
    if (featureDesc != null) {
      c.sendln(featureDesc);
      return true;
    }

    String itemDesc = room.describeItem(name);
    if (itemDesc != null) {
      c.sendln(itemDesc);
      return true;
    }

    String characterDesc = room.describeCharacter(name);
    if (characterDesc != null) {
      c.sendln(characterDesc);
      return true;
    }

    Item inventoryItem = character.getItem(name);
    if (inventoryItem != null) {
      c.sendln(Strings.toFixedWidth(inventoryItem.get("description")));
      return true;
    }

    // Default "You don't see that" message
    c.sendln("You do not see '{g" + name + "{x' here.");
    return true;
  }
}
