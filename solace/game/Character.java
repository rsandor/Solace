package solace.game;

import java.util.*;

import solace.net.Connection;
import solace.util.EventEmitter;
import solace.util.EventListener;

/**
 * Represents a player character or actor in the game world.
 * @author Ryan Sandor Richards.
 */
public class Character {
  // Instance Variables
  String id = null;
  String name;
  String description;

  Room room = null;
  Account account = null;
  List<Item> inventory = null;

  EventEmitter events;

  int level;

  // Stats
  int hp;
  int maxHp;
  int mp;
  int maxMp;
  int sp;
  int maxSp;

  int strength;
  int vitality;
  int magic;
  int speed;

  /**
   * Creates a new character.
   * @param n Name for the character;
   */
  public Character(String n) {
    name = n;
    level = 1;
    inventory = Collections.synchronizedList(new ArrayList<Item>());
    events = new EventEmitter();
  }

  // Statistic accessors and mutators
  public int getHp() { return hp; }
  public void setHp(int v) { hp = v; }
  public int getMaxHp() { return maxHp; }
  public void setMaxHp(int v) { maxHp = v; }

  public int getMp() { return mp; }
  public void setMp(int v) { mp = v; }
  public int getMaxMp() { return maxMp; }
  public void setMaxMp(int v) { maxMp = v; }

  public int getSp() { return sp; }
  public void setSp(int v) { sp = v; }
  public int getMaxSp() { return maxSp; }
  public void setMaxSp(int v) { maxSp = v; }

  public int getStrength() { return strength; }
  public void setStrength(int v) { strength = v; }
  public int getVitality() { return vitality; }
  public void setVitality(int v) { vitality = v; }
  public int getMagic() { return magic; }
  public void setMagic(int v) { magic = v; }
  public int getSpeed() { return speed; }
  public void setSpeed(int v) { speed = v; }

  /**
   * @return The character's level.
   */
  public int getLevel() { return level; }

  /**
   * Set the character's level.
   * @param l Level to set for the character.
   */
  public void setLevel(int l) { level = l; }

  /**
   * Helper method to send messages to a character. This also resends their
   * prompt after the message has been sent.
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

    events.trigger("message", new Object[] { msg });
  }

  /**
   * Sends a string to the character. This method is preferred over
   * `Connection.send` since it allows non-player characters to recieve game
   * messages.
   * @param msg Message to send the character.
   */
  public void send(String msg) {
    Connection c = getConnection();
    if (c != null) {
      c.send(msg);
    }
    events.trigger("message", new Object[] { msg });
  }

  /**
   * Sends a string to the character append with a newline. This method is
   * preferred over `Connection.send` since it allows non-player characters to
   * recieve game messages.
   * @param msg Message to send the character.
   */
  public void sendln(String msg) {
    Connection c = getConnection();
    if (c != null) {
      c.sendln(msg);
    }
    events.trigger("message", new Object[] { msg });
  }

  /**
   * Sends a string to the character wrapped with newlines. This method is
   * preferred over `Connection.send` since it allows non-player characters to
   * recieve game messages.
   * @param msg Message to send the character.
   */
  public void wrapln(String msg) {
    Connection c = getConnection();
    if (c != null) {
      c.wrapln(msg);
    }
    events.trigger("message", new Object[] { msg });
  }

  /**
   * @return The connection associated with this character.
   */
  public Connection getConnection() {
    if (account != null) {
      return World.connectionFromAccount(account);
    }
    return null;
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
    String xml = "<character name=\"" + name + "\" " +
      "hp=\"" + hp + "\" " +
      "maxhp=\"" + maxHp + "\" " +
      "mp=\"" + mp + "\" " +
      "maxmp=\"" + maxMp + "\" " +
      "sp=\"" + sp + "\" " +
      "maxsp=\"" + maxSp + "\" " +
      "strength=\"" + strength + "\" " +
      "vitality=\"" + vitality + "\" " +
      "magic=\"" + magic + "\" " +
      "speed=\"" + speed + "\" " +
      ">";
    if (room != null) {
      xml += "<location area=\"" + room.getArea().getId() +
             "\" room=\"" + room.getId() + "\" />";
    }
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
   * @return The character's description.
   */
  public String getDescription() { return description; }

  /**
   * @param n Description to set for the character.
   */
  public void setDescription(String n) { description = n; }

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

  /**
   * Adds an event listener to the character.
   * @param event Name of the event.
   * @param listener Listener for the event.
   */
  public void addEventListener(String event, EventListener listener) {
    events.addListener(event, listener);
  }

  /**
   * Removes an event listener from the character.
   * @param event Name of the event.
   * @param listener Listener for the event.
   */
  public void removeEventListener(String event, EventListener listener) {
    events.removeListener(event, listener);
  }
}
