package solace.cmd;

import solace.cmd.core.*;
import solace.cmd.admin.*;
import solace.game.Player;
import solace.script.ScriptedCommand;
import solace.script.ScriptedCommands;
import solace.util.Log;
import solace.util.NameTrie;

import java.util.*;

/**
 * Registry for all game play commands. This keeps track of the master
 * list of all commands that can be used within the context of the
 * main game.
 * @author Ryan Sandor Richards
 */
public class CommandRegistry {
  private final Command notFound = new NotFoundCommand();
  private final NameTrie<Command> commands = new NameTrie<>(notFound, Comparator.comparingInt(Command::getPriority));

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

    // Core built-in commands
    Arrays.asList(
      new Quit(),
      new Move(),
      new Look(),
      new Attack(),
      new Help(),
      new Hotbar(),
      new Emote(),
      new Reload(),
      new Shutdown(),
      new Flee()
      //new Inspect()
      //new solace.cmd.deprecated.admin.Set()
    ).forEach(this::add);

    // Add scripted commands
    for (ScriptedCommand command : ScriptedCommands.getCommands()) {
      add(command.getInstance());
    }
  }

  /**
   * Adds a command to the registry.
   * @param c The command to add.
   */
  private synchronized void add(Command c) {
    Log.debug("Adding: " + c.getName());
    String name = c.getName().toLowerCase();
    add(name, c);
    c.getAliases().forEach(alias -> add(alias, c));
  }

  /**
   * Adds a command at the given alias or name.
   * @param name Name for the command.
   * @param command The command.
   */
  private void add(String name, Command command) {
    if (name.length() == 0) {
      Log.warn("Encountered command with empty name, skipping");
      return;
    }
    if (commands.containsName(name)) {
      Log.warn(String.format("Encountered duplicate command name for '%s', skipping", name));
      return;
    }
    Log.trace(String.format("Adding command %s with name %s", command.getName(), name));
    commands.put(name, command);
  }

  /**
   * Finds a command with the name matching the given search string.
   * @param prefix Command name prefix against which to match.
   * @param player Player for which the command is being sought.
   * @return The first command that matches the given string or the "not found" command.
   */
  private synchronized Command findCommand(String prefix, Player player) {
    Command result = commands.find(prefix);
    return result.hasCommand(player) ? result : notFound;
  }

  /**
   * Determines if a player has a command that begins with the given prefix.
   * @param prefix Prefix of the command name to search against.
   * @param player The player in question.
   * @return True if the player has a command, false otherwise.
   */
  private synchronized boolean hasCommand(String prefix, Player player) {
    return findCommand(prefix, player) != notFound;
  }
}
