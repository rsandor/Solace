'use strict';

/**
 * "Slash" is a cooldown action that does 150 potency damage to the attacker's
 * target. If executed after the "flurry of blows" attack it has a combo potency
 * of 250.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('slash', {
  cooldownDuration: Commands.GLOBAL_COOLDOWN,
  initiatesCombat: true,
  basePotency: 150,
  comboPotency: 225,
  combosWith: 'flurry',
  spCost: 4,
  run: function (level, player, target, cooldown) {
    cooldown.executeAttack(target);
  }
});
