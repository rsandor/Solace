'use strict';

/**
 * The `aetherflow` cooldown allows the play to instantly regain 50% of its max
 * mp with a cooldown of 300 seconds.
 */
Commands.addCooldown('aetherflow', {
  cooldownDuration: 300,
  run: function (level, player, target) {
    var maxMp = player.getMaxMp();
    var mp = player.getMp();
    var healed = Math.min(maxMp / 2, maxMp - mp);
    player.setMp(Math.min(maxMp, mp + healed));
    player.sendln(
      'You draw upon the {m}aetherflow{x} healing [{M}' + healed + '{x}] mp!'
    );
  }
});
