package solace.game;
import solace.net.Connection;
import java.util.*;

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
  List<Item> inventory = null;

  /**
   * Creates a new character.
   * @param n Name for the character;
   */
  public Character(String n) {
    name = n;
    inventory = Collections.synchronizedList(new ArrayList<Item>());
  }

  /**
   * Helper method to send messages to a character.
   * @param msg Message to send.
   */
  public void sendMessage(String msg) {
    Connection c = getConnection();

    // The connection could be null, if the character is an actor and not a
    // player...
    if (c != null) {
      c.sendln("\n" + msg);
      c.send(c.getPrompt());
    }
  }

  /**
   * @return The connection associated with this character.
   */
  public Connection getConnection() {
    return World.connectionFromAccount(account);
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
    String xml = "<character name=\"" + name + "\">";
    xml += "<location area=\"" + room.getArea().getId() +
      "\" room=\"" + room.getId() + "\" />";
    return xml + "</character>";
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
  public String getName() { return name; }

  /**
   * @param n Name to set for the character.
   */
  public void setName(String n) { name = n; }

  /**
   * @return A read-only list of the player's inventory.
   */
  public List<Item> getInventory() {
    return Collections.unmodifiableList(inventory);
  }

  /**
   * Finds an item in a players inventory with the given name.
   * @param name Name of the item to find.
   * @return The item in question, or null if no such item was found.
   */
  public Item getItem(String name) {
    synchronized(inventory) {
      for (Item item : inventory) {
        if (item.hasName(name))
          return item;
      }
    }
    return null;
  }

  /**
   * Adds a given item to the player's inventory. If the player has exceeded
   * any game limits on number of items, or carrying capacity, this method
   * will not add the given item and return false.
   * @param item Item to add to the player's inventory.
   * @return True if the item could be added, false otherwise.
   */
  public synchronized boolean addItem(Item item) {
    // TODO: Currently no limits on number of items / weight
    //  need to add this when the time comes.
    inventory.add(item);
    return true;
  }

  /**
   * Removes an item from the player's inventory. Returns false if the item
   * cannot be removed from the player's inventory (e.g. when it is cursed,
   * etc.).
   * @param item Item to remove.
   */
  public synchronized boolean removeItem(Item item) {
    // TODO: No limits on item removal yet, implement them when applicable
    inventory.remove(item);
    return true;
  }
}
