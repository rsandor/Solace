package solace.cmd.deprecated;

import solace.net.*;
import solace.util.*;
import solace.game.*;

/**
 * Wear command, used to equip items.
 * @author Ryan Sandor Richards
 */
public class Wear extends PlayStateCommand {
  public Wear(solace.game.Character ch) {
    super("wear", ch);
  }

  public void run(Connection c, String []params) {
    if (character.isSleeping()) {
      character.sendln("You cannot wear anything while fast asleep.");
      return;
    }

    if (params.length < 2) {
      character.sendln("What would you like to wear?");
      return;
    }

    String itemName = params[1];
    Item item = character.findItem(itemName);

    // Do they have the item?
    if (item == null) {
      character.sendln("You do not possess " + itemName);
      return;
    }

    String itemDesc = item.get("description.inventory");
    String itemLevel = item.get("level");

    // Can it be equipped?
    if (!item.isEquipment()) {
      character.sendln("You cannot wear " + itemDesc);
      return;
    }

    // Are they high enough level to wear it?
    try {
      int levelOffset;
      if (Config.get("item.level-offset") == null) {
        levelOffset = 10;
      }
      else {
        levelOffset = Integer.parseInt(Config.get("world.item.level-offset"));
      }
      if (itemLevel != null) {
        if (Integer.parseInt(itemLevel) > levelOffset + character.getLevel()) {
          character.sendln("You are not powerful enough to wear " + itemDesc);
          return;
        }
      }
    }
    catch (NumberFormatException nfe) {
      Log.warn("Config: world.item.level-offset is not an integer");
    }

    // Attempt to equip the item
    try {
      Item old = character.equip(item);
      if (old != null) {
        character.sendln("You remove " + old.get("description.inventory"));
      }
      character.sendln("You wear " + itemDesc);
    }
    catch (NotEquipmentException e) {
      // This should not happen with checks above, log it and inform the user
      Log.error("Check for valid equipment failed in wear command");
      e.printStackTrace();
      character.sendln("You cannot wear " + itemDesc);
    }
  }
}
