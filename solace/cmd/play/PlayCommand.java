package solace.cmd.play;
import solace.cmd.Command;
import solace.game.Player;

/**
 * Base interface for all game play commands.
 * @author Ryan Sandor Richards
 */
public interface PlayCommand extends Command {
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
