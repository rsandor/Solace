package solace.game;

import java.util.LinkedList;

/**
 * Represnets a one-way connection betwen rooms.
 * @author Ryan Sandor Richards
 */
public class Exit {
	LinkedList<String> names = new LinkedList<String>();
	String toId;
	String description;
	
	/** 
	 * Creates a new exit.
	 * @param names Names by which players can reference the exit.
	 * @param toId The id of the room where this exit leads.
	 */
	public Exit(String names, String toId) {
		this.toId = toId;
		this.description = "";
		setNames(names);
	}
	
	/**
	 * @return The names for the exit.
	 */
	public LinkedList<String> getNames() { return names; }
	
	/**
	 * @param n Names for the exit.
	 */
	public void setNames(String n) { 
		names.clear();
		String[] nameAry = n.trim().split("\\s+");
		for (String name : nameAry)
			names.add(name); 
	}
	
	/**
	 * Adds a name to the exit.
	 * @param n Adds an name to the exit.
	 */
	public void addName(String n) { 
		names.add(n.trim());
	}
	
	/**
	 * Tests to see if a given name matches the exit.
	 * @param n Name to test.
	 * @return <code>true</code> if the exit matches, <code>false</code> otherwise.
	 */
	public boolean matches(String n) {
		for (String name : names)
			if (name.startsWith(n))
				return true;
		return false;
	}
	
	/**
	 * @return The id of the room to which this exit leads.
	 */
	public String getToId() { return toId; }
	
	/**
	 * @param id Id to set for the exit's desitnation.
	 */
	public void setToId(String id) { toId = id; }
	
	/**
	 * @return The exit's description.
	 */
	public String getDescription() { return description; }
	
	/**
	 * @param d The description to set for the exit.
	 */
	public void setDescription(String d) { description = d; }
	
	/**
	 * @param a Additional description to add to the exit.
	 */
	public void addToDescription(String a) { description += a; }
}