package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * The inventory command is used by players to view their characters'
 * in game inventory.
 * @author Ryan Sandor Richards
 */
public class Inventory extends PlayCommand {
  public Inventory(solace.game.Character ch) {
    super("inventory", ch);
  }

  public boolean run(Connection c, String []params) {
    List<Item> inventory = character.getInventory();

    // Send a basic "you have nothing" message if the inventory
    // is empty
    if (inventory.size() == 0) {
      c.sendln("You do not currently possess any items.\n\r");
      return true;
    }

    // Build the result of the inventory output
    StringBuffer buffer = new StringBuffer();
    buffer.append("You currently possess the following items:\n\r");
    synchronized(inventory) {
      for (Item item : inventory) {
        buffer.append("    ");
        buffer.append(item.get("description.inventory"));
        buffer.append("\n\r");
      }
    }
    buffer.append("\n\r");

    // Send the inventory description to the player
    c.sendln(buffer.toString());

    return true;
  }
}
