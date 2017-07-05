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
      c.wrapln(featureDesc);
      return true;
    }

    Item item = room.findItem(name);
    if (item != null) {
      c.wrapln(Strings.toFixedWidth(item.get("description")));
      return true;
    }

    Player player = room.findPlayerIfVisible(name, character);
    if (player != null) {
      String desc = player.getDescription();
      if (desc == null) {
        desc = "They are nondescript.";
      }
      c.wrapln(Strings.toFixedWidth(desc));

      if (!player.isMobile()) {
        if (character.isVisibleTo(player)) {
          player.sendMessage(String.format(
            "%s looks at you.", character.getName()));
        } else {
          // TODO I feel like there should be some sort of check here that
          //      determines wherther or not you even see this message. Some
          //      sort of inate perception check?
          player.sendMessage("You feel as if you are being watched.");
        }
      }

      return true;
    }

    Item inventoryItem = character.findItem(name);
    if (inventoryItem != null) {
      c.wrapln(Strings.toFixedWidth(inventoryItem.get("description")));
      return true;
    }

    // Default "You don't see that" message
    c.sendln("You do not see '{g}" + name + "{x}' here.");
    return true;
  }
}
