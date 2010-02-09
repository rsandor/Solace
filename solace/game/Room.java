package solace.game;

import java.util.*;

/**
 * Basic room class for the engine. 
 * @author Ryan Sandor Richards (Gaius)
 */
public class Room 
{
	int id;
	String title;
	String desc;
	Hashtable exits = new Hashtable();
	
	/**
	 * Creates a new room with the given id, title, and description.
	 * @param i Id for the room.
	 * @param t Title for the room.
	 * @param d Description for the room.
	 */
	public Room(int i, String t, String d)
	{
		id = i;
		title = t;
		desc = d;
	}

	/**
	 * Determines if the room has a particular exit.
	 * @param name Name of the exit.
	 * @return True if the room has such an exit, false otherwise.
	 */
	public boolean hasExit(String name)
	{
		return exits.containsKey(name);
	}
	
	/**
	 * Sets an exit for the room.
	 * @param name Name of the exit.
	 * @param to Destination room for the exit.
	 */
	public void setExit(String name, Room to)
	{
		exits.put(name, to);
	}
	
	/**
	 * Returns the destination room for a given exit.
	 * @param name Name of the exit.
	 * @return Destination room for the exit.
	 */
	public Room getExit(String name)
	{
		return (Room)exits.get(name);
	}
	
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}	
}
