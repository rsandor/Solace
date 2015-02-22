package solace.cmd;

import solace.net.Connection;

/**
 * Commands are essentially programmatic representations of user submitted commands,
 * which contain the code to be executed when the command is executed. Commands have
 * a name, which is used by controllers to determine if/when the command is executed.
 *
 * @author Ryan Sandor Richards (Gaius)
 */
public interface Command
{
	/**
	 * Determines if a connected user has permissions to execute the command.
	 * @param c Connection to test against.
	 * @return True if the user can run the command, false otherwise.
	 */
	public boolean canExecute(Connection c);

	/**
	 * @return The name of the command.
	 */
	public String getName();

	/**
	 * Determines if the given string matches the command's name (this can somtimes
	 * be different than the two strings being lexically identical, consider prefix
	 * matches which many MUDs use).
	 * @param s String to test for a match
	 * @return True if the string matches the command's name, false otherwise.
	 */
	public boolean matches(String s);

	/**
	 * Executes the command.
	 * @param c Connection which issued the command.
	 * @param params Parameters sent along with the command by the connection.
	 * @return True if the command was successful, false otherwise.
	 */
	public boolean run(Connection c, String []params);
}
