'use strict';

/**
 * "Flurry of blows" is a cooldown action that does 150 potency damage to the
 * attacker's target.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('flurry', {
  displayName: 'flurry of blows',
  cooldownDuration: Commands.GLOBAL_COOLDOWN,
  initiatesCombat: true,
  basePotency: 150,
  spCost: 2,
  run: function (level, player, target, cooldown) {
    return cooldown.executeAttack(target);
  }
});
