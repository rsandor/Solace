'use strict';

/**
 * Potency 800 attack spell that costs 20% MP.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('flamestrike', {
  cooldownDuration: Commands.GLOBAL_COOLDOWN,
  initiatesCombat: true,
  basePotency: 800,
  savingThrow: 'reflex',
  mpCost: 20,
  run: function (level, player, target, cooldown) {
    return cooldown.executeAttack(target);
  }
});
