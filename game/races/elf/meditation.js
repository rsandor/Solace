'use strict';

/**
 * Elven racial passive "Meditation"
 */
Passives.add('meditation', function (passive, effect) {
  var scalar = 1.25;

  passive.setLabel("Meditation");

  effect.modMpRecovery(function (player, value) {
    return value * scalar;
  });

  effect.modSpRecovery(function (player, value) {
    return value * scalar;
  });
});
