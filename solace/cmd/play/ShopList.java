package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * List command, lists items for sale in shops to the player.
 * @author Ryan Sandor Richards
 */
public class ShopList extends PlayCommand {
  public ShopList(solace.game.Character ch) {
    super("list", ch);
  }

  public boolean run(Connection c, String []params) {
    Room room = character.getRoom();

    // Ensure the room has a shop
    if (room == null || !room.hasShop()) {
      character.wrapln("There is no shop here.");
      return false;
    }

    character.sendln(room.getShop().list());
    return true;
  }
}
