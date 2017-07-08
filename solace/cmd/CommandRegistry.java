package solace.cmd;

import solace.cmd.core.*;
import solace.cmd.admin.*;
import solace.game.Player;
import solace.script.ScriptedCommand;
import solace.script.ScriptedCommands;
import solace.util.Log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Registry for all game play commands. This keeps track of the master
 * list of all commands that can be used within the context of the
 * main game.
 * @author Ryan Sandor Richards
 */
public class CommandRegistry {
  private final List<Command> commands = Collections.synchronizedList(
    new ArrayList<Command>()
  );
  private final Set<String> names = new HashSet<>();
  private final Command notFound = new NotFoundCommand();

  /**
   * Static singleton instance.
   */
  private static final CommandRegistry instance = new CommandRegistry();

  /**
   * Reloads all commands.
   */
  public static void reload() {
    instance.reloadCommands();
  }

  /**
   * Finds the command with the name matching the given search string.
   * @param search String to match against.
   * @param player Player for which the command is being sought.
   * @return The first command that matches the given string or the "not found" command.
   */
  public static Command find(String search, Player player) {
    return instance.findCommand(search, player);
  }

  /**
   * Determines if the given player has a command for the given search string.
   * @param search String to match against.
   * @param player Player for which the command is being sought.
   * @return `true` if the player has a command that matches, `false` otherwise.
   */
  public static boolean has(String search, Player player) {
    return instance.hasCommand(search, player);
  }

  /**
   * Creates a new game play commands registry.
   */
  private CommandRegistry() {
  }

  /**
   * Reloads all commands to the registry.
   */
  private synchronized void reloadCommands() {
    Log.info("Reloading game commands");
    commands.clear();
    names.clear();

    // Core built-in commands
    add(new Quit());
    add(new Move());
    add(new Look());
    add(new Attack());
    add(new Help());
    add(new Hotbar());
    add(new Emote());

    // Admin Commands
    add(new Reload());
    // add(new Inspect());
    // add(new solace.cmd.deprecated.admin.Set());

    // Add scripted commands
    for (ScriptedCommand command : ScriptedCommands.getCommands()) {
      add(command.getInstance());
    }
  }

  /**
   * Adds a command to the registry.
   * @param c The command to add.
   */
  private void add(Command c) {
    Log.debug("Adding: " + c.getName());
    String name = c.getName().toLowerCase();
    if (name == null || name.length() == 0) {
      Log.warn("Encountered command with empty name, skipping");
      return;
    }
    if (names.contains(name)) {
      Log.warn(String.format("Encountered duplicate command name for '%s', skipping", name));
      return;
    }
    Log.trace(String.format("Adding command: %s", name));
    commands.add(c);
    names.add(name);
  }

  /**
   * Finds a command with the name matching the given search string.
   * @param search String to match against.
   * @param player Player for which the command is being sought.
   * @return The first command that matches the given string or the "not found" command.
   */
  private synchronized Command findCommand(String search, Player player) {
    List<Command> found = commands.stream()
      .filter((c) -> c.matches(search) && c.hasCommand(player))
      .collect(Collectors.toList());
    found.sort((Command a, Command b) -> a.getPriority() < b.getPriority() ? -1 : 1);
    return found.size() >= 1 ? found.get(0) : notFound;
  }

  private synchronized boolean hasCommand(String search, Player player) {
    return commands.stream()
      .filter((c) -> c.matches(search) && c.hasCommand(player))
      .collect(Collectors.toList()).size() > 0;
  }
}
