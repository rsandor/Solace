'use strict';

/**
 * Displays a list of passive abilities for the player.
 * @author Ryan Sandor Richards
 */
Commands.add('passives', function (player, params) {
  var builder = new StringBuilder();

  var k = 0;
  player.getPassives().forEach(function (passive) {
    builder.append(format("%-18s  ", passive));
    if ((++k) % 4 == 0) builder.append("\n\r");
  });

  if (builder.length() > 0) {
    player.sendln("Passive Abilities:", builder.toString());
  } else {
    player.sendln("You have no passive abilities.");
  }
});
