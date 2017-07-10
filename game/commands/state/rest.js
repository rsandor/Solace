'use strict';

/**
 * Allows the player to rest. Resting allows a player to be aware of the game
 * world while increasing the regeneration rate of resource stats such as HP.
 * @author Ryan Sandor Richards
 */
Commands.add('rest', function (player, params) {
  if (player.isResting()) {
    return player.sendln('You are already resting.');
  }

  if (player.isFighting()) {
    return player.sendln('You cannot rest while fighting!');
  }

  var playerMessage = '';
  var roomFormat = '';

  if (player.isSitting()) {
    playerMessage = 'You lie back and rest.';
    roomFormat = '%s lies back and rests.';
  } else if (player.isSleeping()) {
    playerMessage = 'You awake and begin resting.';
    roomFormat = '%s wakes and begins resting.';
  } else if (player.isStanding()) {
    playerMessage = 'You sit down, lie back, and begin resting.';
    roomFormat = '%s sits, lies back, and begins to rest.';
  }

  player.sendln(playerMessage);
  player.getRoom().sendMessage(
    format(roomFormat, player.getName()),
    player
  );
  player.setResting();
});
