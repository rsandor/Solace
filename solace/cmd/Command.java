package solace.cmd;
import solace.game.Player;

import java.util.Collection;

/**
 * Base interface for all game play commands.
 * @author Ryan Sandor Richards
 */
public interface Command {
  /**
   * @return The name of the command.
   */
  String getName();

  /**
   * @return The display name of the command used for messages.
   */
  String getDisplayName();

  /**
   * @return A collection of the command's aliases.
   */
  Collection<String> getAliases();

  /**
   * Returns the ordering priority for the command. This is used to sort
   * a collection of commands returned during a search operation in order
   * to decide which command should be chose (e.g. in for multiple commands
   * that match a common prefix).
   * @return The ordering priority for the command.
   */
  int getPriority();

  /**
   * Determines if a player has access to this command.
   * @param player Player to test.
   * @return `true` if the player has the command, `false` otherwise.
   */
  boolean hasCommand(Player player);

  /**
   * Runs the command for the given player and parameters.
   * @param player Player for which to run the command.
   * @param params Parameters for the command.
   */
  void run(Player player, String []params);
}
