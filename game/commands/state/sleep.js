'use strict';

/**
 * Allows the player to sleep. Sleeping dramatically increases the rate at which
 * resources generate, but at the cost of the player's ability to interact with
 * the game world.
 * @author Ryan Sandor Richards
 */
Commands.add('sleep', function (player, params) {
  if (player.isSleeping()) {
    player.sendln("You are already asleep.");
    return;
  }

  if (player.isFighting()) {
    player.sendln("You cannot sleep while in {R}BATTLE{x}!");
    return;
  }

  var playerMessage = "";
  var roomFormat = "";

  if (player.isSitting()) {
    playerMessage = "You fall asleep.";
    roomFormat = "%s falls asleep.";
  } else if (player.isResting()) {
    playerMessage = "While comfortable resting, you doze off.";
    roomFormat = "%s gets a bit too comfortable resting and dozes off.";
  } else if (player.isStanding()) {
    playerMessage = "You sit, lie back, and fall immediately asleep.";
    roomFormat = "Exhausted, %s sits, lies back, and falls fast asleep.";
  }

  player.getRoom().sendMessage(
    format(roomFormat, player.getName()),
    player
  );
  player.sendln(playerMessage);
  player.setSleeping();
});
