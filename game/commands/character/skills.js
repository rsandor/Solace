'use strict';

/**
 * Lists a character's skills and unlocked abilities therein.
 * @author Ryan Sandor Richards
 */
Commands.add('skills', function (player, params) {
  if (player.isMobile()) {
    return player.sendln('Mobiles do not have skills.');
  }
  var builder = new StringBuilder();
  player.getCharacter().getSkills().forEach(function (s) {
    builder.append(format(
      '[{y}%d/100{x}] {c}%s{x}\n\r',
      s.getLevel(),
      s.getName()
    ));

    builder.append(Strings.toFixedWidth(
      'Passive Enhancements: ' + Joiner.on(', ').join(s.getPassives())
    ));
    builder.append('\n\r');
    builder.append('\n\r');

    builder.append(Strings.toFixedWidth(
      'Cooldown Actions: ' + Joiner.on(', ').join(s.getCooldowns())
    ));
    builder.append('\n\r');
  });

  player.sendln(builder.toString());
});
