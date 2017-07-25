'use strict';

/**
 * Wear command, used to equip items.
 * @author Ryan Sandor Richards
 */
Commands.add('wear', function (player, params) {
  if (player.isMobile()) {
    return player.sendln("Mobiles do not have equipment.");
  }
  var character = player.getCharacter();

  if (character.isSleeping()) {
    character.sendln("You cannot wear anything while fast asleep.");
    return;
  }

  if (params.length < 2) {
    character.sendln("What would you like to wear?");
    return;
  }

  var itemName = params[1];
  var item = character.findItem(itemName);

  // Do they have the item?
  if (item == null) {
    return character.sendln("You do not possess " + itemName);
  }

  var itemDesc = item.get("description.inventory");
  var itemLevel = item.get("level");

  // Can it be equipped?
  if (!item.isEquipment()) {
    return character.sendln("You cannot wear " + itemDesc);
  }

  // Are they high enough level to wear it?
  try {
    var levelOffset = 10;
    if (Config.get("game.item.level-offset") != null) {
      levelOffset = parseInt(Config.get("game.item.level-offset"));
    }
    if (itemLevel != null) {
      if (parseInt(itemLevel) > levelOffset + character.getLevel()) {
        character.sendln("You are not powerful enough to wear " + itemDesc);
        return;
      }
    }
  }
  catch (e) {
    e.printStackTrace();
    Log.warn("Config: game.item.level-offset is not an integer");
  }

  // Attempt to equip the item
  try {
    var old = character.equip(item);
    if (old != null) {
      character.sendln("You remove " + old.get("description.inventory"));
    }
    character.sendln("You wear " + itemDesc);
  } catch (e) {
    Log.error("Error when wearing equipment " + itemDesc);
    character.sendln("You could not wear " + itemDesc);
  }
});
