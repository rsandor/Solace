package solace.cmd.play;

import solace.game.Character;
import solace.net.Connection;

/**
 * Manages and displays a character's hotbar.
 * @author Ryan Sandor Richards
 */
public class Hotbar extends PlayCommand {
  /**
   * Set of all valid hotbar keys.
   */
  public static final String[] HOTBAR_KEYS = {
    "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "="
  };

  /**
   * Default constructor.
   * @param ch Character for the command.
   */
  public Hotbar(solace.game.Character ch) {
    super("hotbar", ch);
  }

  /**
   * Displays the hotbar settings for the character.
   * @return True if the command succeeds, false otherwise.
   */
  public void displayHotbar() {
    StringBuilder buf = new StringBuilder();
    buf.append("Hotbar Assignments:\n\r\n\r");
    for (String key : HOTBAR_KEYS) {
      String command = character.getHotbarCommand(key);
      if (command == null || command.length() == 0) {
        command = "<none>";
        buf.append(String.format("  [%s]: %s\n\r", key, command));
      } else {
        buf.append(String.format("  {y}[%s]:{x} {m}%s{x}\n\r", key, command));
      }
    }
    character.sendln(buf.toString());
  }

  /**
   * Runs the command.
   * @param c The connection to the character's client.
   * @param params Parameters for the command.
   * @return True if the command succeeds, false otherwise.
   */
  public void run(Connection c, String []params) {
    if (params.length == 1) {
      displayHotbar();
      return;
    } else if (params.length == 3) {
      String key = params[1];
      String command = params[2];
      character.setHotbarCommand(key, command);
    }
    character.sendln("Usage: hotbar [<key> <command>]");
  }
}
