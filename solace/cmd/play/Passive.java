package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * Displays a list of passive abilities for the character.
 * @author Ryan Sandor Richards
 */
public class Passive extends PlayCommand {
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
