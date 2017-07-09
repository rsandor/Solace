'use strict';

var EQ_SLOTS = Packages.solace.game.Character.EQ_SLOTS;

/**
 * Equipment command, displays a character's current equipment.
 * @author Ryan Sandor Richards
 */
Commands.add('equipment', function (player, params) {
  var buffer = new StringBuffer();
  buffer.append(Strings.banner('Equipment'));

  EQ_SLOTS.forEach(function (slot) {
    var item = player.getCharacter().getEquipment(slot);
    var name = !item ? '---' : item.get('description.inventory');
    var line = format('| [ {y}%-8s{x} ]: %s', slot, name);
    buffer.append(line);
    buffer.append(Strings.spaces(
      80 - 1 - Color.strip(line).length()) + '|\n\r');
  });
  buffer.append(Strings.RULE);

  player.sendln(buffer.toString());
});
