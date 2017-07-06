'use strict';

/**
 * The survivor cooldown instantly heals 50% of both HP and SP with a cooldown
 * duration of 300 seconds.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('survivor', {
  cooldownDuration: 300,
  run: function (level, player) {
    var hpHealed = Math.min(
      player.getMaxHp() / 2,
      player.getMaxHp() - player.getHp());
    var spHealed = Math.min(
      player.getMaxSp() / 2,
      player.getMaxSp() - player.getSp());

    player.setHp(player.getHp() + hpHealed);
    player.setSp(player.getSp() + spHealed);
    player.sendln(
      'You are a {m}survivor{x}! You\'ve been healed [{G}' + hpHealed +
      '{x}] hp and [{Y}' + spHealed + '{x}] sp!'
    );
  }
});
