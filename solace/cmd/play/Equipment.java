package solace.cmd.play;

import solace.net.*;
import solace.util.*;
import solace.game.*;

/**
 * Equipment command, displays a character's current equipment.
 * @author Ryan Sandor Richards
 */
public class Equipment extends PlayCommand {
  public Equipment(solace.game.Character ch) {
    super("equipment", ch);
  }

  public boolean run(Connection c, String []params) {
    StringBuffer b = new StringBuffer();
    b.append(Strings.banner("Equipment"));

    for (String slot : solace.game.Character.EQ_SLOTS) {
      Item item = character.getEquipment(slot);
      String name = (item == null) ?
        "---" : item.get("description.inventory");
      String line = String.format("| [ {y%-8s{x ]: %s", slot, name);
      b.append(line);
      b.append(Strings.spaces(80 - 1 - Color.strip(line).length()) + "|\n\r");
    }

    b.append(Strings.RULE);

    character.sendln(b.toString());

    return true;
  }
}
