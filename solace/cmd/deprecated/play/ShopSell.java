package solace.cmd.deprecated.play;

import solace.game.*;
import solace.net.*;

/**
 * Sell command, sells items in player inventories to shop owners.
 * @author Ryan Sandor Richards
 */
public class ShopSell extends ShopCommand {
  public ShopSell(solace.game.Character ch) {
    super("sell", ch);
  }

  public void command(
    Connection c,
    String[] params,
    Shop shop,
    Room room
  ) {
    if (character.isFighting()) {
      character.sendln("You're too distracted to sell items while fighting!");
      return;
    }

    if (params.length < 2) {
      character.wrapln("What item would you like to sell?");
      return;
    }

    String name = params[1];
    Item item = character.findItem(name);

    if (item == null) {
      character.wrapln(String.format(
        "You do not currently possess %s", name
      ));
      return;
    }

    character.resetVisibilityOnAction("sell");

    long price = shop.buyPrice(item);
    if (price < 1) {
      character.wrapln(String.format(
        "The shop owner is uninterested in %s",
        item.get("description.inventory")
      ));
      return;
    }

    character.removeItem(item);
    character.addGold(price);
    character.wrapln(String.format(
      "You sell %s for {y}%dg{x}.",
      item.get("description.inventory"),
      price
    ));
  }
}
