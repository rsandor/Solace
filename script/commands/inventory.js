'use strict';

/**
 * Implements the inventory inspection command for the game.
 */
Commands.add("inventory", function (character, params) {
  if (character.getInventory().size() == 0) {
    character.sendln("You do not currently possess any items.\n\r");
    return true;
  }

  var buf = ["You currently possess the following items:\n\r"];
  character.getInventory().forEach(function (item) {
    buf.push('    ' + item.get("description.inventory"));
  });

  character.sendln(buf.join('\n\r') + '\n\r');

  return true;
});
