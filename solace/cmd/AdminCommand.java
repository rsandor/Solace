package solace.cmd;

import solace.net.Connection;
import solace.game.Account;

/**
 * This serves as the base class for all immortal commands in the game.
 * 
 * TODO This will have to be updated if the immortal structure is ever changed
 * from being binary to some other hierarchy.
 * 
 * @author Ryan Sandor Richards
 */
public abstract class AdminCommand 
	extends AbstractCommand 
{
	public AdminCommand(String n)
	{
		super(n);
	}
	
	/**
	 * Returns true only if the connection has an account with type administrator
	 */
	public boolean canExecute(Connection c)
	{
		return (c.hasAccount() && c.getAccount().isAdmin());
	}
}
