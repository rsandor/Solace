package solace.cmd;

import solace.net.Connection;

/**
 * Defines the behaviors of state controller commands.
 * @author Ryan Sandor Richards
 */
public interface StateCommand {
  /**
   * Determines if a connected account has permissions to execute the command.
   * @param c Connection to test against.
   * @return True if the user can run the command, false otherwise.
   */
  public boolean canExecute(Connection c);

  /**
   * @return The name of the command.
   */
  public String getName();

  /**
   * @return The display name of the command used for messages.
   */
  public String getDisplayName();

  /**
   * Sets the display name for the command.
   * @param String n Name to set.
   */
  public void setDisplayName(String n);

  /**
   * Determines if the given string matches the command's name (this can
   * somtimes be different than the two strings being lexically identical,
   * consider prefix matches which many MUDs use).
   * @param s String to test for a match
   * @return True if the string matches the command's name, false otherwise.
   */
  public boolean matches(String s);

  /**
   * Executes the command.
   * @param c Connection which issued the command.
   * @param params Parameters sent along with the command by the connection.
   */
  public void run(Connection c, String []params);
}
