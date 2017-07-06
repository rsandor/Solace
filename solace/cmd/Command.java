package solace.cmd;

/**
 * Base interface for all commands.
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
}
