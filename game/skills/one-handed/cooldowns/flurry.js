'use strict';

/**
 * "Flurry of blows" is a cooldown action that does 125 potency damage to the
 * attacker's target.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('flurry', {
  displayName: 'flurry of blows',
  cooldownDuration: Commands.GLOBAL_COOLDOWN,
  initiatesCombat: true,
  basePotency: 125,
  spCost: 2,
  run: function (player, target, level, cooldown) {
    return cooldown.executeAttack(player, target);
  }
});
