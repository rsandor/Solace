'use strict';

/**
 * Potency 800 attack spell that costs 20% MP.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('flamestrike', {
  cooldownDuration: Commands.GLOBAL_COOLDOWN,
  initiatesCombat: true,
  castTime: 3,
  basePotency: 400,
  savingThrow: 'reflex',
  castMessage: 'You begin casting flamestrike...',
  mpCost: 20,
  run: function (player, target, level, cooldown) {
    return cooldown.executeAttack(player, target);
  }
});
