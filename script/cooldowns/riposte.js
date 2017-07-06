'use strict';

/**
 * "Riposte" is a cooldown action that does 150 potency damage to the attacker's
 * target. If executed immediately after a "slash" cooldown then this has a
 * combo potency of 350.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('riposte', {
  cooldownDuration: Commands.GLOBAL_COOLDOWN,
  initiatesCombat: true,
  basePotency: 150,
  comboPotency: 350,
  combosWith: 'slash',
  spCost: 6,
  run: function (level, player, target, cooldown) {
    return cooldown.executeAttack(target);
  }
});
