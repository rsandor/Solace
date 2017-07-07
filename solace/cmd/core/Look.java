package solace.cmd.core;

import solace.cmd.AbstractCommand;
import solace.game.*;
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
public class Look extends AbstractCommand {
  public Look() {
    super("look");
  }

  public void run(Player player, String []params) {
    Room room = player.getRoom();

    if (player.isSleeping()) {
      player.sendln("You cannot see anything, for you are asleep.");
      return;
    }

    if (params.length == 1) {
      player.sendln(room.describeTo(player));
      room.sendMessage(player.getName() + " looks around.", player);
      return;
    }

    String name = params[1];

    String featureDesc = room.describeFeature(name);
    if (featureDesc != null) {
      player.wrapln(featureDesc);
      return;
    }

    Item item = room.findItem(name);
    if (item != null) {
      player.wrapln(Strings.toFixedWidth(item.get("description")));
      return;
    }

    Player other = room.findPlayerIfVisible(name, player);
    if (other != null) {
      String desc = other.getDescription();
      if (desc == null) {
        desc = "They are nondescript.";
      }
      player.wrapln(Strings.toFixedWidth(desc));

      if (!other.isMobile()) {
        if (player.isVisibleTo(other)) {
          other.sendMessage(String.format(
            "%s looks at you.", player.getName()));
        } else {
          // TODO I feel like there should be some sort of check here that
          //      determines whether or not you even see this message. Some
          //      sort of innate perception check?
          other.sendMessage("You feel as if you are being watched.");
        }
      }
      return;
    }

    solace.game.Character character = player.getCharacter();
    if (character != null) {
      Item inventoryItem = character.findItem(name);
      if (inventoryItem != null) {
        player.wrapln(Strings.toFixedWidth(inventoryItem.get("description")));
        return;
      }
    }

    // Default "You don't see that" message
    player.sendln("You do not see '{g}" + name + "{x}' here.");
  }
}
