'use strict';

/**
 * The `drop` command allows players to drop items from their inventory into
 * the game world.
 */

Commands.add('drop', function (player, params) {
  if (params.length == 1) {
    player.sendln('What would you like to drop?');
    return false;
  }

  if (player.isSleeping()) {
    player.sendln('You cannot drop anything, for you are fast asleep.');
    return false;
  }

  var name = params[1];
  var item = player.findItem(name);

  if (item == null) {
    player.sendln('You do not currently possess \'' + name + '\'.');
    return false;
  }

  player.resetVisibilityOnAction('drop');

  var description = item.get('description.inventory');
  player.removeItem(item);
  player.getRoom().addItem(item);

  player.sendln('You drop ' + description);
  player.getRoom().sendMessage(
    player.getName() + ' drops ' + description + '\n\r', player);

  return true;
});
