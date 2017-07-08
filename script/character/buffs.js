'use strict';

var Buff = Packages.solace.game.Buff;

/**
 * Helper that formats remaining time.
 */
function formatTimeRemaining (time) {
  var timeRemaining = new StringBuilder();
  timeRemaining.append("{b}({c}");
  if (time > 3600) {
    timeRemaining.append(format(
      "%dh%dm", time / 3600, (time % 3600) / 60));
  } else if (time > 60) {
    timeRemaining.append(format("%dm%ds", time / 60, time % 60));
  } else if (time >= 0) {
    timeRemaining.append(format("%ds", time));
  } else if (time <= Buff.TIME_REMAINING_INDEFINATE) {
    timeRemaining.append("special");
  }
  timeRemaining.append("{b}){x}");
  return timeRemaining.toString();
}

/**
 * Displays a list of all buffs and debuffs currently affecting the player.
 * @author Ryan Sandor Richards
 */
Commands.add('buffs', {
  aliases: ['affects'],
  run: function (player, params) {
    var buffs = new StringBuilder();
    var debuffs = new StringBuilder();
    player.getBuffs().forEach(function (buff) {
      var buffer = buff.isDebuff() ? debuffs : buffs;
      buffer.append(format(
        "%s %-16s {M}%-18s{x} %-43s\n\r",
        buff.isDebuff() ? "{R}-{x}" : "{g}+{x}",
        formatTimeRemaining(buff.getTimeRemaining()),
        buff.getName(),
        buff.getDescription()
      ));
    });

    if (buffs.length() == 0 && debuffs.length() == 0) {
      player.sendln("You are affected by no buffs or debuffs.");
    } else {
      if (buffs.length() > 0) {
        player.sendln("Current Buffs:", buffs.toString());
      }
      if (debuffs.length() > 0) {
        player.sendln("Current Debuffs:", debuffs.toString());
      }
    }
  }
});
