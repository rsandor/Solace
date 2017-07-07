package solace.cmd;
import solace.game.Player;

/**
 * Base interface for all game play commands.
 * @author Ryan Sandor Richards
 */
public interface Command {
  /**
   * @return The name of the command.
   */
  public String getName();

  /**
   * @return The display name of the command used for messages.
   */
  public String getDisplayName();

  /**
   * Determines if the given string matches the command's name (this can
   * somtimes be different than the two strings being lexically identical,
   * consider prefix matches which many MUDs use).
   * @param s String to test for a match
   * @return True if the string matches the command's name, false otherwise.
   */
  public boolean matches(String s);

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
