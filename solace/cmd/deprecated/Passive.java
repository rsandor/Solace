package solace.cmd.deprecated;

import solace.net.*;

/**
 * Displays a list of passive abilities for the character.
 * @author Ryan Sandor Richards
 */
public class Passive extends PlayStateCommand {
  public Passive(solace.game.Character ch) {
    super("passive", ch);
  }

  public void run(Connection c, String []params) {
    StringBuilder buffer = new StringBuilder();

    int k = 0;
    for (String passive : character.getPassives()) {
      buffer.append(String.format("%-18s  ", passive));
      k++;
      if (k % 4 == 0) buffer.append("\n\r");
    }

    if (buffer.length() > 0) {
      character.sendln("Passive Abilities:");
      character.sendln(buffer.toString());
    } else {
      character.sendln("You have no passive abilities.");
    }
  }
}
