'use strict';

/**
 * Dwarven racial passive "Stout Hearted"
 */
Passives.add('stout-hearted', function (passive, effect) {
  passive.setLabel("Stout Hearted");
  effect.modVitality(function (player, vit) {
    return vit * 1.1;
  });
});
