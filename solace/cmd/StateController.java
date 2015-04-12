package solace.cmd;

import solace.net.Connection;

/**
 * State controllers are used to control game flow via user input.
 * @author Ryan Sandor Richards (Gaius)
 */
public interface StateController
{
  /**
   * Initializes this state controller to work with a given connection.
   * @param c Connection the controller will be working for.
   */
  public void init(Connection c);

  /**
   * Forces a user to enter and execute a command.
   * @param command Command to execute.
   */
  public void force(String input);

  /**
   * Parses user input and attempts to execute an appropriate command (if any).
   * @param input Input to parse.
   */
  public void parse(String input);
}
