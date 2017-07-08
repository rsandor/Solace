package solace.cmd;

import com.google.common.base.Joiner;
import solace.game.Player;
import solace.util.NameTrie;

/**
 * Abstract base class for commands that are composites of SubCommands. Allows for various
 * similar command implementations to be scoped under a single root command.
 * @author Ryan Sandor Richards
 */
public class CompositeCommand extends AbstractCommand {
  private static NameTrie<SubCommand> subCommands;

  /**
   * Creates a new composite command.
   * @param name Name of the command.
   */
  public CompositeCommand(String name) {
    super(name);
    subCommands = new NameTrie<>(this::defaultCommand);
  }

  /**
   * Add a sub command to this composite command.
   * @param name Name of the sub command.
   * @param subCommand The sub command to add.
   */
  protected void addSubCommand(String name, SubCommand subCommand) {
    subCommands.put(name, subCommand);
  }

  /**
   * The default command to execute when a sub command is not found.
   * @param player Player that initiated the command.
   * @param params Original parameters to the command.
   */
  protected void defaultCommand(Player player, String[] params) {
    player.sendln(String.format(
      "Sub command '{y}%s{x}' not found.", Joiner.on(" ").join(params)));
  }

  /**
   * Finds a sub command with the given name Prefix.
   * @param namePrefix Name prefix for search.
   * @return The first sub command with a name that matches the given prefix.
   *   If no such command could be found this returns a command that calls the
   *   defaultCommand method.
   */
  @SuppressWarnings("WeakerAccess")
  protected SubCommand getSubCommand(String namePrefix) {
    return subCommands.find(namePrefix);
  }

  /**
   * Runs a the sub command for the given player and parameters.
   * @param player Player who is initiating the command.
   * @param params Parameters to this command.
   */
  @SuppressWarnings("WeakerAccess")
  protected void runSubCommand(Player player, String[] params) {
    if (params.length != 2) {
      defaultCommand(player, params);
      return;
    }
    getSubCommand(params[1]).run(player, params);
  }

  @Override
  public void run(Player player, String[] params) {
    runSubCommand(player, params);
  }
}
