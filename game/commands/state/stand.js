'use strict';

/**
 * Allows the player to stand up from a sitting, resting, or sleeping state.
 * @author Ryan Sandor Richards
 */
Commands.add('stand', {
  aliases: ['wake'],
  run: function (player, params) {
    if (player.isStandingOrFighting()) {
      player.sendln("You are already standing.");
      return;
    }

    var playerMessage = "";
    var roomFormat = "";

    if (player.isSitting()) {
      playerMessage = "You stand up.";
      roomFormat = "%s stands up.";
    } else if (player.isSleeping()) {
      playerMessage = "You awake and stand up.";
      roomFormat = "%s wakes and stands up.";
    } else if (player.isResting()) {
      playerMessage = "You stop resting and stand up.";
      roomFormat = "%s stops resting and stands up.";
    }

    player.getRoom().sendMessage(
      format(roomFormat, player.getName()),
      player
    );
    player.sendln(playerMessage);
    player.setStanding();
  }
});
