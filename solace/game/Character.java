package solace.game;
import solace.net.Connection;

/**
 * Represents a player character or actor in the game world.
 * @author Ryan Sandor Richards.
 */
public class Character {
    // Instance Variables
    String id = null;
    String name;
    Room room = null;
    Account account = null;

    /**
     * Creates a new character.
     * @param n Name for the character;
     */
    public Character(String n) {
        name = n;
    }

    /**
     * Helper method to send messages to a character.
     * @param msg Message to send.
     */
    public void sendMessage(String msg) {
        Connection c = World.connectionFromAccount(account);

        // The connection could be null, if the character is an actor and not a player...
        if (c != null) {
            c.sendln("\n" + msg);
            c.send(c.getPrompt());
        }
    }

    /**
     * @param a The account for the character.
     */
    public void setAccount(Account a) {
        account = a;
    }

    /**
     * @return The account to which the character belongs.
     */
    public Account getAccount() {
        return account;
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
    public String getName() {   return name; }

    /**
     * @param n Name to set for the character.
     */
    public void setName(String n) { name = n; }
}
