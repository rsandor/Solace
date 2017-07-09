'use strict';

/**
 * Scan command, shows players and mobiles in adjacent rooms.
 * @author Ryan Sandor Richards
 */
Commands.add('scan', function (player, params) {
  if (player.isRestingOrSitting()) {
    return player.sendln("You cannot scan around unless standing.");
  }

  if (player.isFighting()) {
    return player.sendln("You are too busy to scan while engaged in combat!");
  }

  if (player.isSleeping()) {
    return player.sendln("You cannot scan whilst asleep.");
  }

  var room = player.getRoom();
  var area = room.getArea();
  var exits = room.getExits();
  var builder = new StringBuilder();

  exits.forEach(function (exit) {
    var r = area.getRoom(exit.getToId());
    if (r.getPlayers().size() == 0) return;
    builder.append(exit.getDescription().trim());
    builder.append(":\n\r");
    r.getPlayers().forEach(function (p) {
      builder.append("  ");
      builder.append(p.getName());
      builder.append("\n\r");
    });
  });

  room.sendMessage(
    format('%s scans the surrounding area.', player.getName()), player);
  var message = builder.toString();
  if (message == "") {
    message = "There is nobody of interest in any direction.";
  }
  player.sendln(message);
});
