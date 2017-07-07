package solace.cmd.deprecated.admin;

import solace.cmd.deprecated.PlayStateCommand;
import java.util.*;
import solace.game.*;
import solace.net.*;
import solace.util.*;

/**
 * The inspect command gives a plain and clear readout of underlying game data
 * to characters associated with admin accounts.
 *
 * TODO Add admin only help and an entry for this command.
 *
 * @author Ryan Sandor Richards
 */
public class Inspect extends PlayStateCommand {
  public Inspect(solace.game.Character ch) {
    super("inspect", ch);
  }

  public void run(Connection c, String []params) {
    solace.game.Character character = getCharacter();

    if (!character.getAccount().isAdmin()) {
      Log.error("Admin level play command executed by a normal account.");
      c.sendln("You are unable to perform this command.");
      return;
    }

    Room room = character.getRoom();
    if (params.length == 1) {
      c.sendln(inspectRoom(room));
      return;
    }

    String name = params[1];

    Item item = room.findItem(name);
    if (item != null) {
      c.sendln(inspectItem(item));
      return;
    }

    Player player = room.findPlayer(name);
    if (player != null) {
      c.sendln(inspectPlayer(player));
      return;
    }

    Item inventoryItem = character.findItem(name);
    if (inventoryItem != null) {
      c.sendln(inspectItem(inventoryItem));
      return;
    }

    c.sendln("Game entity with name '{g}" + name + "{x}' was not found.");
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
      "| {c}Title:{x}          %-60s |\n\r", room.getTitle()));
    buffer.append(String.format(
      "| {c}ID:{x}             %-60s |\n\r", room.getId()));
    buffer.append(String.format(
      "| {c}Area:{x}           %-60s |\n\r", room.getArea().getTitle()));

    if (room.hasShop()) {
      Shop shop = room.getShop();
      Mobile owner = shop.getOwner();
      buffer.append(Strings.RULE);
      buffer.append(String.format(
        "| {c}Shop ID:{x}        %-60s |\n\r", shop.getId()));
      buffer.append(String.format(
        "| {c}Shop Name:{x}      %-60s |\n\r", shop.getName()));
      if (owner != null) {
        buffer.append(String.format(
          "| {c}Shop Owner:{x}     %-60s |\n\r", shop.getOwner().getName()));
      } else {
        buffer.append(String.format(
          "| {c}Shop Owner:{x}     %-60s |\n\r", "(none)"));
      }
      buffer.append(String.format(
        "| {c}Buy Multipler:{x}  %-60.2f |\n\r", shop.getBuyMultiplier()));
      buffer.append(String.format(
        "| {c}Sell Multipler:{x} %-60.2f |\n\r", shop.getSellMultiplier()));
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
        String.format("{c}%s:{x} %s", key, value)));
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

    if (player.isMobile()) {
      buffer.append(String.format("| {c}Name:{x}   %-72s |\n\r",
        player.getName() + " {y}(mobile){x}"));
    } else {
      buffer.append(String.format("| {c}Name:{x}   %-68s |\n\r",
        player.getName()));
    }
    buffer.append(String.format(
      "| {c}State:{x}  %-68s |\n\r", player.getPlayState().toString()));
    buffer.append(String.format(
      "| {c}Room:{x}   %-68s |\n\r", player.getRoom().getTitle()));

    buffer.append(Strings.RULE);

    if (player.isMobile()) {
      buffer.append(String.format(
        "| {c}Level:{x} %-29d | {c}Power:{x} %-30d |\n\r",
        player.getLevel(), mobile.getPower()));
    } else {
      buffer.append(String.format(
        "| {c}Level:{x}  %-68s |\n\r", player.getLevel()));
    }

    buffer.append(Strings.RULE);

    buffer.append(String.format(
      "| {c}Strength:{x} %-26s | {c}Vitality:{x} %-27s |\n\r",
      player.getStrength(), player.getVitality()));
    buffer.append(String.format(
      "| {c}Magic:{x}    %-26s | {c}Speed:{x}    %-27s |\n\r",
      player.getMagic(), player.getSpeed()));

    buffer.append(Strings.RULE);

    buffer.append(String.format(
      "| {c}Hit Points:{x}  %-63s |\n\r",
      (player.getHp() + " / " + player.getMaxHp())));
    buffer.append(String.format(
      "| {c}Armor Class:{x} %-63s |\n\r", player.getAC()));
    buffer.append(String.format(
      "| {c}Attack Roll:{x} %-63s |\n\r", player.getAttackRoll()));
    buffer.append(String.format(
      "| {c}Hit Mod:{x}     %-63s |\n\r", player.getHitMod()));
    buffer.append(String.format(
      "| {c}Damage Mod:{x}  %-63s |\n\r", player.getDamageMod()));
    buffer.append(String.format(
      "| {c}Avg Damage:{x}  %-63s |\n\r", player.getAverageDamage()));
    buffer.append(String.format(
      "| {c}Num Attacks:{x} %-63s |\n\r", player.getNumberOfAttacks()));

    buffer.append(Strings.RULE);

    buffer.append(String.format(
      "| {c}Will Save:{x}     %-22s | {c}Reflex Save:{x} %-23s |\n\r",
      player.getWillSave(), player.getReflexSave()));
    buffer.append(String.format(
      "| {c}Resolve Save:{x}  %-22s | {c}Vigor Save:{x}  %-23s |\n\r",
      player.getResolveSave(), player.getVigorSave()));
    buffer.append(String.format(
      "| {c}Prudence Save:{x} %-22s | {c}Guile Save:{x}  %-23s |\n\r",
      player.getPrudenceSave(), player.getGuileSave()));

    buffer.append(Strings.RULE);

    return buffer.toString();
  }
}
