package solace.cmd;

import solace.game.Player;

/**
 * Functional interface for sub-command functions. These are useful
 * for making map based implementations for commands that have multiple
 * modes.
 * @author Ryan Sandor Richards;
 */
public interface SubCommand {
  /**
   * Runs the sub-command;
   * @param player Player who is running the command.
   * @param params Original parameters to the command.
   */
  void run(Player player, String[] params);
}
