'use strict';

var EQ_SLOTS = Packages.solace.game.Character.EQ_SLOTS

/**
 * Remove command, allows players to remove equipment from their characters.
 * @author Ryan Sandor Richards
 */
Commands.add('remove', function (player, params) {
  if (player.isMobile()) {
    return player.sendln("Mobiles do not have equipment.");
  }
  var character = player.getCharacter();

  if (params.length < 2) {
    return character.sendln("What would you like to remove?");
  }

  if (character.isSleeping()) {
    return character.sendln("You cannot remove equipment whilst alseep.");
  }

  var itemName = params[1];
  var removeItem = null;
  EQ_SLOTS.forEach(function (slot) {
    var item = character.getEquipment(slot);
    if (item == null) return;
    if (item.hasName(itemName)) {
      removeItem = item;
    }
  });

  if (removeItem == null) {
    return character.sendln("You are not wearing " + itemName + ".");
  }

  var itemDesc = removeItem.get("description.inventory");
  try {
    character.unequip(removeItem);
    character.sendln("You removed " + itemDesc);
  } catch (e) {
    Log.error("Error when removing equipment " + itemDesc);
    character.sendln("You could not remove " + itemDesc);
  }
});
