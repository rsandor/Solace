package solace.cmd.core;

import solace.game.Room;
import solace.game.Shop;

/**
 * List command, lists items for sale in shops to the player.
 * @author Ryan Sandor Richards
 */
public class ShopList extends ShopCommand {
  public ShopList() {
    super("list");
  }

  public void command(solace.game.Character character, String[] params, Shop shop, Room room) {
    if (character.isFighting()) {
      character.sendln("You cannot browse a shop while fighting!");
      return;
    }
    character.sendln(room.getShop().list());
  }
}
