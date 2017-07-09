'use strict';

/**
 * Applies the "concentrate" buff that doubles the potency of cooldon actions
 * for ten seconds.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('concentrate', {
  cooldownDuration: 180,
  run: function (player, target, level, cooldown) {
    player.applyBuff('concentrating');
    return true;
  }
});
