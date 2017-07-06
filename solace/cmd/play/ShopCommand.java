package solace.cmd.play;

import solace.game.*;
import solace.net.*;

/**
 * Buy command, allows the player to buy items from shops.
 * @author Ryan Sandor Richards
 */
public abstract class ShopCommand extends PlayStateCommand {
  public ShopCommand(String cmd, solace.game.Character ch) {
    super(cmd, ch);
  }

  public void run(Connection c, String []params) {
    Room room = character.getRoom();

    if (character.isRestingOrSitting()) {
      character.sendln("You cannot shop unless standing.");
      return;
    }

    if (character.isSleeping()) {
      character.sendln("You dream about shopping, whilst asleep.");
      return;
    }

    if (character.isFighting()) {
      character.sendln("You don't have time for shopping, you're in combat!");
      return;
    }

    // Ensure the room has a shop
    if (room == null || !room.hasShop()) {
      character.sendln("There is no shop here.");
      return;
    }

    command(c, params, room.getShop(), room);
  }

  /**
   * Override this method to add specific code to the shop command.
   */
  public abstract void command(
    Connection c,
    String[] params,
    Shop shop,
    Room room
  );
}
