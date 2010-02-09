package solace.cmd;

import solace.net.Connection;
import java.util.*;


/**
 * Base class for all state controllers used in the Solace Engine.
 * Works as a nice implementation of StateController adding some
 * features that allow controllers to work more fluidly with the
 * <code>Command</code> class.
 * 
 * @author Ryan Sandor Richards
 */
public abstract class AbstractStateController 
	implements StateController 
{
	Connection connection;
	LinkedList commands = new LinkedList();
	String invalidCommandMessage;
	
	/**
	 * @return the invalidCommandMessage
	 */
	public String getInvalidCommandMessage() {
		return invalidCommandMessage;
	}

	/**
	 * @param invalidCommandMessage the invalidCommandMessage to set
	 */
	public void setInvalidCommandMessage(String invalidCommandMessage) {
		this.invalidCommandMessage = invalidCommandMessage;
	}

	/**
	 * Creates a new controller.
	 * @param c Connection the controller is to work with.
	 */
	public AbstractStateController(Connection c)
	{
		init(c);
		invalidCommandMessage = "Unknown command.";
	}
	
	/**
	 * Creates a new controller with the specified connection and invalid command
	 * message.
	 * @param c Connection for the controller.
	 * @param icm Invalid command message for the controller.
	 */
	public AbstractStateController(Connection c, String icm)
	{
		init(c);
		invalidCommandMessage = icm;
	}
	
	/**
	 * Initializes this controller to work with the given connection.
	 */
	public void init(Connection c)
	{
		connection = c;
	}
	
	/**
	 * Adds a command to this controller.
	 * @param c Command to add.
	 */
	public void addCommand(Command c)
	{
		commands.add(c);
	}
	
	/**
	 * Attempts to find a command that matches with the given string.
	 * @param c Search criteria.
	 * @return A command that matches, or null if no commands match the string.
	 */
	protected Command findCommand(String c)
	{
		Iterator iter = commands.iterator();
		
		while (iter.hasNext())
		{
			Command cmd = (Command)iter.next();
			if (cmd.matches(c))
				return cmd;
		}
		
		return null;
	}
	
	/**
	 * Parses commands using a "prefix" search routine.
	 * @param input Input to parse.
	 */
	public void parse(String input) 
	{
		if (input == null || connection == null)
			return;
		
		String []params = input.split("\\s+");
		
		if (params.length < 1)
			return;
		
		Command cmd = findCommand(params[0]);
		
		if (cmd != null && cmd.canExecute(connection))
			cmd.run(connection, params);
		else
			connection.sendln(invalidCommandMessage);
	}
}
