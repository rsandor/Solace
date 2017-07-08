package solace.cmd.core;

import solace.cmd.AbstractCommand;
import solace.cmd.Command;
import solace.cmd.CommandRegistry;
import solace.game.Player;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Manages and displays a character's hotbar.
 * @author Ryan Sandor Richards
 */
public class Hotbar extends AbstractCommand {
  /**
   * Set of all valid hotbar keys.
   */
  private static final HashSet<String> HOTBAR_KEYS = new HashSet<>(
    Arrays.asList(
      "1", "2", "3", "4", "5", "6",
      "7", "8", "9", "0", "-", "="
    )
  );

  /**
   * Creates a new hotbar command.
   */
  public Hotbar() {
    super("hotbar");
    setPriority(AbstractCommand.ORDER_CORE + 10); // help > hotbar
  }

  /**
   * @see solace.cmd.Command
   */
  public void run(Player player, String []params) {
    solace.game.Character character = player.getCharacter();
    if (character == null) {
      player.sendln("Hotbar command not available to non-characters.");
      return;
    }
    if (params.length == 1) {
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
    } else if (params.length == 3) {
      String key = params[1];
      if (!HOTBAR_KEYS.contains(key)) {
        player.sendln("Valid hotbar key values are:");
        player.send("    ");
        player.sendln(Joiner.on(", ").join(
          HOTBAR_KEYS.stream()
            .map((k) -> String.format("'{y}%s{x}'", k))
            .collect(Collectors.toList())
        ));
        return;
      }
      String search = params[2];
      if (!CommandRegistry.has(search, player)) {
        player.sendln(String.format(
          "No command matches '{m}%s{x}', hotbar key {y}[%s]{x} not set!", search, key));
        return;
      }
      Command command = CommandRegistry.find(search, player);
      character.setHotbarCommand(key, command.getName());
      player.sendln(String.format(
        "Hotbar key {y}[%s]{x} set to command '{m}%s{x}',",
        key, command.getName()));
    } else {
      player.sendln("Usage: hotbar [<key> <command>]");
    }
  }
}
