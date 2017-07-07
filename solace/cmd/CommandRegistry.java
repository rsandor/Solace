package solace.cmd;

import solace.cmd.deprecated.Look;
import solace.cmd.deprecated.Move;
import solace.cmd.deprecated.Quit;

import solace.game.Player;
import solace.script.ScriptedCommand;
import solace.script.ScriptedCommands;
import solace.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

/**
 * Registry for all game play commands. This keeps track of the master
 * list of all commands that can be used within the context of the
 * main game.
 * @author Ryan Sandor Richards
 */
public class CommandRegistry {
  private final List<Command> commands;
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
    notFound = new NotFoundCommand();
  }

  /**
   * Reloads all commands to the registry.
   */
  private synchronized void reloadCommands() {
    Log.info("Reloading game commands");
    commands.clear();

    // Core built-in commands
    commands.add(new Quit());
    commands.add(new Move());
    commands.add(new Look());

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
      Log.debug("Adding scripted command to registry");
      Log.debug(command.getName());
      commands.add(command.getInstance());
    }
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
