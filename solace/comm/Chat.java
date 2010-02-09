package solace.comm;

import solace.net.Connection;

/**
 * Basic out of character channel for out of game chat.
 * 
 * @author Ryan Sandor Richards (Gaius)
 */
public class Chat 
	implements Channel
{
	public boolean canSeeMessage(Connection c) 
	{
		return false; 
	}

	public boolean canSeeSender(Connection s, Connection c) 
	{
		return true;
	}

	public String getFormat() 
	{
		return "{c%n{y: {x%m";
	}
}
