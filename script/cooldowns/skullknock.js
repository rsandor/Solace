'use strict';

var Buffs = Packages.solace.util.Buffs;

/**
 * A potency 150 attack that stuns the target for 4 seconds.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('skullknock', {
  cooldownDuration: 180,
  initiatesCombat: true,
  basePotency: 150,
  run: function (level, player, target, cooldown) {
    if (cooldown.executeAttack(target)) {
      target.applyBuff(Buffs.create("stunned", 4));
    }
  }
});
