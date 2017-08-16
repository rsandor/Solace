'use strict';

/**
 * Gnomish racial passive "Metamagical"
 */
Passives.add('metamagical', function (passive, effect) {
  passive.setLabel("Metamagical");

  effect.modMpCost(function (player, cost) {
    return 0.9 * cost;
  });
});
