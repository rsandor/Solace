package solace.cmd;

import solace.cmd.core.*;
import solace.game.Player;
import solace.script.ScriptedCommand;
import solace.script.ScriptedCommands;
import solace.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * Registry for all game play commands. This keeps track of the master
 * list of all commands that can be used within the context of the
 * main game.
 * @author Ryan Sandor Richards
 */
public class CommandRegistry {
  private final List<Command> commands;
  private final Hashtable<String, Boolean> names;
  private final Command notFound;
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
   * Creates a new game play commands registry.
   */
  private CommandRegistry() {
    commands = Collections.synchronizedList(new ArrayList<Command>());
    names = new Hashtable<>();
    notFound = new NotFoundCommand();
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

    /*
    // Emotes
    Emote emote = new Emote();
    add(emote);
    add(Emotes.getInstance().getEmoteAliases(), new Emote());
    */

    /*
    // Admin Commands
    add(new Inspect());
    add(new solace.cmd.deprecated.admin.Set());
    */

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
    String name = c.getName();
    if (name == null || name.length() == 0) {
      Log.warn("Encountered command with empty name, skipping");
      return;
    }
    Log.trace(String.format("Adding command: %s", name));
    if (names.containsKey(name)) {
      Log.warn(String.format("Encountered duplicate command name for '%s'", name));
      return;
    }
    commands.add(c);
    names.put(name, true);
  }

  /**
   * Finds a command with the name matching the given search string.
   * @param search String to match against.
   * @param player Player for which the command is being sought.
   * @return The first command that matches the given string or the "not found" command.
   */
  private synchronized Command findCommand(String search, Player player) {
    List<Command> found = new LinkedList<>();
    for (Command command : commands) {
      if (command.matches(search)) {
        found.add(command);
      }
    }
    // TODO We need a way to rank commands with like prefixes...
    for (Command command : found) {
      if (command.hasCommand(player)) {
        return command;
      }
    }
    return notFound;
  }
}
