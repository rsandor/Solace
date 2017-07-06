'use strict';

/**
 * The `get` command allows players to obtain items from the game world and
 * place them into their inventories.
 */
Commands.add('get', function (player, params) {
  if (params.length < 2) {
    return player.sendln('What would you like to get?\n\r');
  }

  if (player.isSleeping()) {
    return player.sendln('You cannot get anything while alseep.');
  }

  var name = params[1];
  var room = player.getRoom();
  var item = room.findItem(name);

  // If there is no such item, then we're done, inform the player
  if (!item) {
    return player.sendln('You could not find \'' + name + '\' here.\n\r');
  }

  player.resetVisibilityOnAction('get');

  // Remove the item from the room and add it to the player's inventory
  room.removeItem(item);
  player.addItem(item);

  // Inform the users and the rest of the players
  var description = item.get('description.inventory');
  player.sendln('You get ' + description);
  room.sendMessage(player.getName() + ' gets ' + description, player);
});
