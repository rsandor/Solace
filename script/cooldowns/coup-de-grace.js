'use strict';

/**
 * "Coup de Grace" is a cooldown action that does 1000 potency damage to a
 * target whos health is at 30% or less.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('coup', {
  displayName: 'coup de grace',
  cooldownDuration: 120,
  initiatesCombat: true,
  basePotency: 1000,
  spCost: 10,
  run: function (player, target, level, cooldown) {
    return cooldown.executeAttack(target);
  },
  checkValidTarget: function (target) {
    if (target.getHp() / target.getMaxHp() >= 0.3) {
      throw new Error(
        'Coup de grace can only be used on targets with less than 30% health.'
      );
    }
  }
});
