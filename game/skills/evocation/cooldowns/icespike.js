'use strict';

/**
 * Potency 200 attack spell that heals 5-10% of MP (based on skill level).
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('icespike', {
  cooldownDuration: Commands.GLOBAL_COOLDOWN,
  initiatesCombat: true,
  basePotency: 70,
  savingThrow: 'prudence',
  run: function (player, target, level, cooldown) {
    var isHit = cooldown.executeAttack(player, target);
    if (isHit) {
      var pctHeal = 0.05 + (0.05 * level / 100.0);
      var mpHeal = parseInt(player.getMaxMp() * pctHeal, 10);
      player.setMp(Math.min(player.getMaxMp(), player.getMp() + mpHeal));
    }
    return isHit;
  }
});
