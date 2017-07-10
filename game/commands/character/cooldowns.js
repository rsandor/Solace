'use strict';

/**
 * Displays a list of non-global cooldowns and the time remaning for each.
 * @author Ryan Sandor Richards
 */
Commands.add('cooldowns', function (player, params) {
  var builder = new StringBuilder();
  player.getCooldowns().forEach(function (name) {
    var duration = player.getCooldownDuration(name);
    if (duration < 1) return;
    builder.append(format('  [{C}%3ds{x}] {m}%s{x}\n\r', duration, name));
  });
  if (builder.length() == 0) {
    return player.sendln('No actions on cooldown at this time.');
  }
  player.sendln('Actions on cooldown:\n\r', builder.toString());
});
