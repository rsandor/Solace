'use strict';

/**
 * "Riposte" is a cooldown action that does 125 potency damage to the attacker's
 * target. If executed immediately after a "slash" cooldown then this has a
 * combo potency of 300.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('riposte', {
  cooldownDuration: Commands.GLOBAL_COOLDOWN,
  initiatesCombat: true,
  basePotency: 150,
  comboPotency: 300,
  combosWith: 'slash',
  spCost: 6,
  run: function (player, target, level, cooldown) {
    return cooldown.executeAttack(player, target);
  }
});
