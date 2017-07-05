'use strict';

/**
 * Applies the "shocked" debuff to a target.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown("shock", {
  initiatesCombat: true,
  cooldownDuration: 6,
  castTime: 3,
  basePotency: 100,
  savingThrow: 'will',
  castMessage: 'You begin casting shock...',
  run: function (level, player, givenTarget, cooldown) {
    try {
      var target = cooldown.resolveTarget(givenTarget);
      var result = cooldown.rollToHit(target);
      if (result.isMiss()) {
        cooldown.sendMissMessage();
        return false;
      }

      // TODO Seems like this calculation should be centralized...
      // TODO Should we allow for the use of passives on this?
      var POTENCY_LOW = 20.0;
      var POTENCY_HIGH = 30.0;
      var potency = POTENCY_LOW + POTENCY_HIGH * (level/100.0);
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
