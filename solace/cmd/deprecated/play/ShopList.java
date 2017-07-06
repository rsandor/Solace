package solace.cmd.deprecated.play;

import solace.game.*;
import solace.net.*;

/**
 * List command, lists items for sale in shops to the player.
 * @author Ryan Sandor Richards
 */
public class ShopList extends ShopCommand {
  public ShopList(solace.game.Character ch) {
    super("list", ch);
  }

  public void command(
    Connection c,
    String[] params,
    Shop shop,
    Room room
  ) {
    if (character.isFighting()) {
      character.sendln("You cannot browse a shop while fighting!");
      return;
    }
    character.sendln(room.getShop().list());
  }
}
