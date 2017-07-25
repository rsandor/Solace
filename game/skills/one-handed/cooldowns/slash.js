'use strict';

/**
 * "Slash" is a cooldown action that does 125 potency damage to the attacker's
 * target. If executed after the "flurry of blows" attack it has a combo potency
 * of 200.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('slash', {
  cooldownDuration: Commands.GLOBAL_COOLDOWN,
  initiatesCombat: true,
  basePotency: 125,
  comboPotency: 200,
  combosWith: 'flurry',
  spCost: 4,
  run: function (player, target, level, cooldown) {
    return cooldown.executeAttack(player, target);
  }
});
