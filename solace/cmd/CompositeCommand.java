package solace.cmd;

import com.google.common.base.Joiner;
import solace.game.Player;
import solace.util.Log;

import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.SortedMap;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;


/**
 * Abstract base class for commands that are composites of SubCommands. Allows for various
 * similar command implementations to be scoped under a single root command.
 * @author Ryan Sandor Richards
 */
public class CompositeCommand extends AbstractCommand {
  private static Trie<String, SubCommand> subCommandTrie;

  /**
   * Creates a new composite command.
   * @param name Name of the command.
   */
  public CompositeCommand(String name) {
    super(name);
    subCommandTrie = new PatriciaTrie<>(new LinkedHashMap<String, SubCommand>());
  }

  /**
   * Add a sub command to this composite command.
   * @param name Name of the sub command.
   * @param subCommand The sub command to add.
   */
  protected void addSubCommand(String name, SubCommand subCommand) {
    if (subCommandTrie.keySet().contains(name)) {
      Log.warn(String.format("Duplicate name for SubCommand encountered: %s, skipping", name));
      return;
    }
    subCommandTrie.put(name, subCommand);
  }

  /**
   * The default command to execute when a sub command is not found.
   * @param player Player that initiated the command.
   * @param params Original parameters to the command.
   */
  protected void defaultCommand(Player player, String[] params) {
    player.sendln(String.format("Sub command '{y}%s{x}' not found.", Joiner.on(" ").join(params)));
  }

  /**
   * Finds a sub command with the given name Prefix.
   * @param namePrefix Name prefix for search.
   * @return The first sub command with a name that matches the given prefix.
   *   If no such command could be found this returns a command that calls the
   *   defaultCommand method.
   */
  protected SubCommand getSubCommand(String namePrefix) {
    try {
      SortedMap<String, SubCommand> matches = subCommandTrie.prefixMap(namePrefix);
      return matches.get(matches.firstKey());
    } catch (NoSuchElementException e) {
      return this::defaultCommand;
    }
  }

  /**
   * Runs a the sub command for the given player and parameters.
   * @param player Player who is initiating the command.
   * @param params Parameters to this command.
   */
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
