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
  String getName();

  /**
   * @return The display name of the command used for messages.
   */
  String getDisplayName();

  /**
   * Returns the ordering priority for the command. This is used to sort
   * a collection of commands returned during a search operation in order
   * to decide which command should be chose (e.g. in for multiple commands
   * that match a common prefix).
   * @return The ordering priority for the command.
   */
  int getPriority();

  /**
   * Determines if the given string matches the command's name (this can
   * sometimes be different than the two strings being lexically identical,
   * consider prefix matches which many MUDs use).
   * @param s String to test for a match
   * @return True if the string matches the command's name, false otherwise.
   */
  boolean matches(String s);

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
