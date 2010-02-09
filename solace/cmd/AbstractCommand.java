package solace.cmd;

import solace.net.Connection;

/**
 * Basic abstract command class that serves as the base class for all commands.
 * This class handles command name storage and retrieval, as well as the command
 * matching by using the well known "prefix" match.
 * 
 * @author Ryan Sandor Richards (Gaius)
 */
public abstract class AbstractCommand 
	implements Command 
{
	String name;
	
	/**
	 * Creates a new <code>AbstractCommand</code> with the given name.
	 * @param n Name for the command.
	 */
	public AbstractCommand(String n)
	{
		name = n.toLowerCase();
	}
	
	/**
	 * Default behavior set to always return <code>true</code>, override in sub classes to
	 * add varied functionality.
	 */
	public boolean canExecute(Connection c)
	{
		return true;
	}
	
	/**
	 * @see solace.cmd.Command.getName()
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Implementation of the basic "prefix" match used by many other MUDs.
	 **/
	public boolean matches(String s) 
	{
		if (s == null || s.length() == 0)
			return false;
		
		s = s.toLowerCase();
		
		for (int i = 0; i < s.length(); i++)
		{
			if (i >= name.length() || name.charAt(i) != s.charAt(i))
				return false;
		}
		
		return true;
	}

	/**
	 * @see solace.cmd.Command.run(Connection c, String []params)
	 */
	public abstract void run(Connection c, String[] params); 
}
