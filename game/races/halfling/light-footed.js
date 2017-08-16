'use strict';

/**
 * Halfling racial passive "Light Footed"
 */
Passives.add('light-footed', function (passive, effect) {
  passive.setLabel("Light Footed");
  effect.modSpeed(function (player, spe) {
    return spe * 1.1;
  });
});
