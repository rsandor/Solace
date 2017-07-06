'use strict';

/**
 * The `drop` command allows players to drop items from their inventory into
 * the game world.
 */

Commands.add('drop', function (player, params) {
  if (params.length == 1) {
    return player.sendln('What would you like to drop?');
  }

  if (player.isSleeping()) {
    return player.sendln('You cannot drop anything, for you are fast asleep.');
  }

  var name = params[1];
  var item = player.findItem(name);

  if (item == null) {
    return player.sendln('You do not currently possess \'' + name + '\'.');
  }

  player.resetVisibilityOnAction('drop');

  var description = item.get('description.inventory');
  player.removeItem(item);
  player.getRoom().addItem(item);

  player.sendln('You drop ' + description);
  player.getRoom().sendMessage(
    player.getName() + ' drops ' + description + '\n\r', player
  );
});
