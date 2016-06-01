package solace.cmd.play;

import solace.net.*;
import solace.util.*;
import solace.game.*;

/**
 * Remove command, allows players to remove equipment from their characters.
 * @author Ryan Sandor Richards
 */
public class Remove extends PlayCommand {
  public Remove(solace.game.Character ch) {
    super("remove", ch);
  }

  public boolean run(Connection c, String []params) {
    if (params.length < 2) {
      character.sendln("What would you like to remove?");
      return false;
    }

    if (character.getPlayState() == PlayState.SLEEPING) {
      character.sendln("You cannot remove equipment whilst alseep.");
      return false;
    }

    String itemName = params[1];
    Item removeItem = null;

    for (String slot : solace.game.Character.EQ_SLOTS) {
      Item item = character.getEquipment(slot);
      if (item == null) {
        continue;
      }
      if (item.hasName(itemName)) {
        removeItem = item;
        break;
      }
    }

    if (removeItem == null) {
      character.sendln("You are not wearing " + itemName + ".");
      return false;
    }

    String itemDesc = removeItem.get("description.inventory");

    try {
      character.unequip(removeItem);
      character.sendln("You removed " + itemDesc);
    }
    catch (NotEquipmentException ne) {
      // Shouldn't happen because of checks above...
      Log.error("Attempted to remove non-equipment item: " + itemDesc);
      ne.printStackTrace();
      character.sendln("You cannot remove " + itemDesc);
      return false;
    }
    catch (NoSuchItemException nse) {
      // Shouldn't happen because of checks above...
      Log.error("Attempted to remove unheld item: " + itemDesc);
      nse.printStackTrace();
      character.sendln("You cannot remove: " + itemDesc);
      return false;
    }

    return true;
  }
}
