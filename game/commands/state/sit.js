'use strict';

/**
 * Allows the player to sit down either on the ground or in objects such as
 * chairs, benches, etc.
 * @author Ryan Sandor Richards
 */
Commands.add('sit', function (player, params) {
  if (player.isSitting()) {
    player.sendln("You are already sitting.");
    return;
  }

  if (player.isFighting()) {
    player.sendln("You cannot sit down in the middle of battle!");
    return;
  }

  var playerMessage = "";
  var roomFormat = "";

  if (player.isStanding()) {
    playerMessage = "You sit down.";
    roomFormat = "%s sits down.";
  } else if (player.isSleeping()) {
    playerMessage = "You awake and sit up.";
    roomFormat = "%s wakes and sits up.";
  } else if (player.isResting()) {
    playerMessage = "You stop resting and sit up.";
    roomFormat = "%s stops resting and sits up.";
  }

  player.getRoom().sendMessage(
    format(roomFormat, player.getName()),
    player
  );
  player.sendln(playerMessage);
  player.setSitting();
});
