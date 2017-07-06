package solace.cmd;

import solace.net.Connection;

/**
 * Defines the behaviors of state controller commands.
 * @author Ryan Sandor Richards
 */
public interface StateCommand extends Command {
  /**
   * Sets the display name for the command.
   * @param String n Name to set.
   */
  public void setDisplayName(String n);

  /**
   * Determines if a connected account has permissions to execute the command.
   * @param c Connection to test against.
   * @return True if the user can run the command, false otherwise.
   */
  public boolean canExecute(Connection c);

  /**
   * Executes the command.
   * @param c Connection which issued the command.
   * @param params Parameters sent along with the command by the connection.
   */
  public void run(Connection c, String []params);
}
