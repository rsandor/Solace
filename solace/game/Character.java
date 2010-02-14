package solace.game;

/**
 * Represents a player character or actor in the game world.
 * @author Ryan Sandor Richards.
 */
public class Character {
	// Instance Variables
	String id = null;
	String name;
	Room room = null;
	
	/**
	 * Creates a new character.
	 * @param n Name for the character;
	 */
	public Character(String n) {
		name = n;
	}
	
	/**
	 * @param r The room to set.
	 */
	public void setRoom(Room r) {
		room = r;
	}
	
	/**
	 * @return the Character's current room.
	 */
	public Room getRoom() {
		return room;
	}
	
	/**
	 * @return XML representation of the character.
	 */
	public String getXML() {
		String xml = "";
		xml += "<character name=\"" + name + "\"></character>";
		return xml;
	}
	
	/**
	 * @return The character's id.
	 */
	public String getId() { return id; }
	
	/**
	 * @param i Id to set for the character.
	 */
	public void setId(String i) { id = i; }
	
	/**
	 * @return The character's name.
	 */
	public String getName() {	return name; }
	
	/**
	 * @param n Name to set for the character.
	 */
	public void setName(String n) { name = n; }
}
