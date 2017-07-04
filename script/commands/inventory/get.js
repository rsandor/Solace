'use strict';

/**
 * The `get` command allows players to obtain items from the game world and
 * place them into their inventories.
 */
Commands.add('get', function (player, params) {
  if (params.length < 2) {
    player.sendln('What would you like to get?\n\r');
    return false;
  }

  if (player.isSleeping()) {
    player.sendln('You cannot get anything while alseep.');
    return false;
  }

  var name = params[1];
  var room = player.getRoom();
  var item = room.findItem(name);

  // If there is no such item, then we're done, inform the player
  if (!item) {
    player.sendln('You could not find \'' + name + '\' here.\n\r');
    return false;
  }

  player.resetVisibilityOnAction('get');

  // Remove the item from the room and add it to the player's inventory
  room.removeItem(item);
  player.addItem(item);

  // Inform the users and the rest of the players
  var description = item.get('description.inventory');
  player.sendln('You get ' + description);
  room.sendMessage(player.getName() + ' gets ' + description, player);

  return true;
});
