package solace.cmd.admin;

import solace.cmd.play.PlayCommand;
import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * The inspect command gives a plain and clear readout of underlying game data
 * to characters associated with admin accounts.
 *
 * TODO Add admin only help and an entry for this command.
 *
 * @author Ryan Sandor Richards
 */
public class Inspect extends PlayCommand {
  public Inspect(solace.game.Character ch) {
    super("inspect", ch);
  }

  public boolean run(Connection c, String []params) {
    solace.game.Character character = getCharacter();
    
    if (!character.getAccount().isAdmin()) {
      c.sendln("You are unable to perform this command.");
      Log.error("Admin level play command executed by a normal account.");
      return false;
    }

    Room room = character.getRoom();
    if (params.length == 1) {
      c.sendln(inspectRoom(room));
      return true;
    }

    String name = params[1];

    Item item = room.findItem(name);
    if (item != null) {
      c.sendln(inspectItem(item));
      return true;
    }

    Player player = room.findPlayer(name);
    if (player != null) {
      c.sendln(inspectPlayer(player));
      return true;
    }

    Item inventoryItem = character.findItem(name);
    if (inventoryItem != null) {
      c.sendln(inspectItem(inventoryItem));
      return true;
    }

    c.sendln("Game entity with name '{g" + name + "{x' was not found.");
    return true;
  }

  /**
   * Provides the inspect description for the given room.
   * @param room The room to inspect.
   * @return The resulting inspect description.
   */
  protected String inspectRoom(Room room) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(Strings.RULE);

    buffer.append(String.format(
      "| {cTitle:{x          %-60s |\n\r", room.getTitle()));
    buffer.append(String.format(
      "| {cID:{x             %-60s |\n\r", room.getId()));
    buffer.append(String.format(
      "| {cArea:{x           %-60s |\n\r", room.getArea().getTitle()));

    if (room.hasShop()) {
      Shop shop = room.getShop();
      Mobile owner = shop.getOwner();
      buffer.append(Strings.RULE);
      buffer.append(String.format(
        "| {cShop ID:{x        %-60s |\n\r", shop.getId()));
      buffer.append(String.format(
        "| {cShop Name:{x      %-60s |\n\r", shop.getName()));
      if (owner != null) {
        buffer.append(String.format(
          "| {cShop Owner:{x     %-60s |\n\r", shop.getOwner().getName()));
      } else {
        buffer.append(String.format(
          "| {cShop Owner:{x     %-60s |\n\r", "(none)"));
      }
      buffer.append(String.format(
        "| {cBuy Multipler:{x  %-60.2f |\n\r", shop.getBuyMultiplier()));
      buffer.append(String.format(
        "| {cSell Multipler:{x %-60.2f |\n\r", shop.getSellMultiplier()));
    }

    buffer.append(Strings.RULE);
    return buffer.toString();
  }

  /**
   * Provides the inspect description for an item.
   * @param  item Item to inspect.
   * @return      The inspect description.
   */
  protected String inspectItem(Item item) {
    StringBuffer buffer = new StringBuffer();
    Hashtable<String, String> props = item.getProperties();
    for (String key : props.keySet()) {
      String value = props.get(key);
      buffer.append(Strings.toFixedWidth(
        String.format("{c%s:{x %s", key, value)));
      buffer.append("\n\r");
    }
    return buffer.toString();
  }

  /**
   * Provides the inspect description for a player.
   * @param  player The player to inspect.
   * @return        The inpsect description.
   */
  protected String inspectPlayer(Player player) {
    Mobile mobile = player.isMobile() ? (Mobile)player : null;
    StringBuffer buffer = new StringBuffer();
    buffer.append(Strings.RULE);

    buffer.append(String.format("| {cName:{x   %-68s |\n\r", player.getName()));
    buffer.append(String.format(
      "| {cLevel:{x  %-68s |\n\r", player.getLevel()));
    if (player.isMobile()) {
      buffer.append(String.format(
        "| {cPower:{x  %-68s |\n\r", mobile.getPower()));
    }
    buffer.append(String.format(
      "| {cState:{x  %-68s |\n\r", player.getPlayState().toString()));
    buffer.append(String.format(
      "| {cRoom:{x   %-68s |\n\r", player.getRoom().getTitle()));
    buffer.append(String.format(
      "| {cMobile:{x %-68s |\n\r", player.isMobile() ? "yes" : "no"));

    buffer.append(Strings.RULE);

    buffer.append(String.format(
      "| {cHit Points:{x  %-63s |\n\r",
      (player.getHp() + " / " + player.getMaxHp())));
    buffer.append(String.format(
      "| {cArmor Class:{x %-63s |\n\r", player.getAC()));
    buffer.append(String.format(
      "| {cAttack Roll:{x %-63s |\n\r", player.getAttackRoll()));
    buffer.append(String.format(
      "| {cHit Mod:{x     %-63s |\n\r", player.getHitMod()));
    buffer.append(String.format(
      "| {cDamage Mod:{x  %-63s |\n\r", player.getDamageMod()));
    buffer.append(String.format(
      "| {cAvg Damage:{x  %-63s |\n\r", player.getAverageDamage()));
    buffer.append(String.format(
      "| {cNum Attacks:{x %-63s |\n\r", player.getNumberOfAttacks()));

    buffer.append(Strings.RULE);
    return buffer.toString();
  }
}
