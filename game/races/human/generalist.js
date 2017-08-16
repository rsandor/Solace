'use strict';

/**
 * Human racial passive "Generalist"
 */
Passives.add('generalist', function (passive, effect) {
  passive.setLabel("Generalist");

  effect.modBaseAttackRoll(function (player, roll) {
    return 1.05 * roll;
  });

  // TODO Need to flesh out all other checks when we get the chance...
});
