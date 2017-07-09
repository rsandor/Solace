package solace.cmd.core;

import solace.cmd.AbstractCommand;
import solace.game.*;

/**
 * Buy command, allows the player to buy items from shops.
 * @author Ryan Sandor Richards
 */
public abstract class ShopCommand extends AbstractCommand {
  ShopCommand(String cmd) {
    super(cmd);
  }

  public void run(Player player, String []params) {
    if (player.isMobile()) {
      player.sendln("Mobiles cannot shop.");
    }
    Room room = player.getRoom();

    if (player.isRestingOrSitting()) {
      player.sendln("You cannot shop unless standing.");
      return;
    }
    if (player.isSleeping()) {
      player.sendln("You dream about shopping, whilst asleep.");
      return;
    }
    if (player.isFighting()) {
      player.sendln("You don't have time for shopping, you're in combat!");
      return;
    }

    // Ensure the room has a shop
    if (room == null || !room.hasShop()) {
      player.sendln("There is no shop here.");
      return;
    }

    command(player.getCharacter(), params, room.getShop(), room);
  }

  public abstract void command(solace.game.Character character, String[] params, Shop shop, Room room);
}
