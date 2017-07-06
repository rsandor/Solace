package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * Appraise command, lets the player know how much the owner of the shop will
 * pay for items in their inventory.
 * @author Ryan Sandor Richards
 */
public class ShopAppraise extends ShopCommand {
  public ShopAppraise(solace.game.Character ch) {
    super("appraise", ch);
  }

  public void command(
    Connection c,
    String[] params,
    Shop shop,
    Room room
  ) {
    if (character.isFighting()) {
      character.sendln("You cannot have items appraised while fighting!");
      return;
    }

    if (params.length < 2) {
      character.wrapln("What item would you like appraised?");
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

    character.resetVisibilityOnAction("appraise");

    long price = shop.buyPrice(item);
    if (price < 1) {
      character.wrapln(String.format(
        "The shop owner is uninterested in %s",
        item.get("description.inventory")
      ));
    }
    else {
      character.wrapln(String.format(
        "The shop owner will pay {y}%dg{x} for %s.",
        price,
        item.get("description.inventory")
      ));
    }
  }
}
