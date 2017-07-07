'use strict';

/**
 * Cooldown that allows a player to "vanish" from sight. They gain the
 * "vanished" buff until they move, initiate combat, or are attacked (but there
 * are very few entities that can see a halfling who vanishes). If used while in
 * combat the player is immediately removed from the battle.
 * @author Ryan Sandor Richards
 */
Commands.addCooldown('vanish', {
  cooldownDuration: 180,
  run: function (player, target, level, cooldown) {
    player.applyBuff("vanished");
    var battle = player.getBattle();
    if (battle !== null) {
      battle.remove(player);
    }
  }
});
