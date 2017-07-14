'use strict';

/**
 * Applies the "shocked" debuff to a target.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown("shock", {
  initiatesCombat: true,
  cooldownDuration: 10,
  castTime: 3,
  basePotency: 100,
  savingThrow: 'will',
  castMessage: 'You begin casting shock...',
  run: function (player, givenTarget, level, cooldown) {
    try {
      var target = cooldown.resolveTarget(player, givenTarget);
      var result = cooldown.rollToHit(player, target);
      if (result.isMiss()) {
        cooldown.sendMissMessage(player);
        return false;
      }

      // TODO Seems like this calculation should be centralized...
      // TODO Should we allow for the use of passives on this?
      var POTENCY_LOW = 10.0;
      var POTENCY_HIGH = 20.0;
      var potency = POTENCY_LOW + (POTENCY_HIGH * level / 100.0);
      var avgDamage = parseInt(potency * player.getAverageDamage() / 100.0, 10);

      // TODO Perhaps too specific an interface...
      target.applyDot(
        "shocked", avgDamage, 30, 2,
        "<{r}%d{x}> you are shocked by the envoloping electricity!");

      return true;
    } catch (e) {
      player.sendln(e.getMessage());
      return false;
    }
  }
});
