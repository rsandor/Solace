'use strict';

/**
 * Implements the inventory inspection command for the game.
 */
Commands.add('inventory', function (player, params) {
  var inventory = player.getCharacter().getInventory();
  if (inventory.size() == 0) {
    return player.sendln('You do not have any items.\n\r');
  }
  var buf = ['You have the following items:\n\r'];
  inventory.forEach(function (item) {
    buf.push('    ' + item.get('description.inventory'));
  });
  player.sendln(buf.join('\n\r') + '\n\r');
});
