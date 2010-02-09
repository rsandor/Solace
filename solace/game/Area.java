package solace.game;

import java.util.*;

/**
 * Holds information pretaining to an area.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Area 
{
	String name = "";
	String creator = "";
	LinkedList rooms = new LinkedList();
	
	/**
	 * Creates a new area with the given name and name of the creator.
	 * @param n Name of the area.
	 * @param c Name of the creator.
	 */
	public Area(String n, String c)
	{
		name = n;
		creator = c;
	}
	
	/**
	 * Adds a room to the area.
	 * @param r Room to add.
	 */
	public void addRoom(Room r)
	{
		rooms.add(r);
	}
	
	/**
	 * Removes a room from this area.
	 * @param r Room to remove.
	 */
	public void removeRoom(Room r)
	{
		rooms.remove(r);
	}
	
	/**
	 * Returns a collection of the rooms in this area.
	 * @return A collection of the rooms in this area.
	 */
	public Collection getRooms()
	{
		return rooms;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
