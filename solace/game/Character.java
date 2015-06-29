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
  /**
   * Unmodifiable list of all valid equipment slots for a given character.
   */
  public static final List<String> EQ_SLOTS = Collections.unmodifiableList(
    new LinkedList<String>(Arrays.asList(
      new String[] {
        "helm",
        "body",
        "hands",
        "waist",
        "legs",
        "feet",
        "neck",
        "ears",
        "wrist",
        "ring",
        "weapon",
        "off-hand"
      }
    ))
  );

  /**
   * Determines if the given slot name is a valid equipment slot.
   * @param name Name of the slot to check.
   * @return `true` if the slot is valid, `false` otherwise.
   */
  public static boolean isValidEquipmentSlot(String name) {
    return EQ_SLOTS.contains(name);
  }

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

  // Currency
  long gold;

  // Equipment
  Hashtable<String, Item> equipment = new Hashtable<String, Item>();

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
   * @return The amount of gold the character is carrying.
   */
  public long getGold() { return gold; }

  /**
   * Sets the amount of gold the character is carrying.
   * @param g Gold to set.
   */
  public void setGold(long g) { gold = g; }

  /**
   * Removes a given amount of gold from the character. Does nothing if the character
   * doesn't have enough gold.
   * @param g Gold to remove from the character.
   * @throws CurrencyException If the player has less gold than what was given.
   */
  public void removeGold(long g)
    throws CurrencyException
  {
    if (gold < g) {
      throw new CurrencyException("Unable to remove " + g + " gold from " + name);
    }
    gold -= g;
  }

  /**
   * Adds gold to a character.
   * @param g Amount of gold to add.
   */
  public void addGold(long g) {
    gold += g;
  }

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
   * Equips a particular item onto the character. If a piece of equipment
   * already inhabits the particular slot it is removed and returned to the
   * character's inventory.
   * @param item Item to equip.
   * @return The item that was previously equipped.
   */
  public Item equip(Item item)
    throws NotEquipmentException
  {
    String slot = item.get("slot");
    if (slot == null) {
      throw new NotEquipmentException("Unable to equip non-equipment item.");
    }

    Item old = null;
    if (equipment.containsKey(slot)) {
      old = equipment.get(slot);
      addItem(old);
    }
    removeItem(item);
    equipment.put(slot, item);
    return old;
  }

  /**
   * Removes the givem item from a character and places it in their inventory.
   * @param item Item to remove.
   * @throws NoSuchItemException If the character does not possess the item.
   * @throws NotEquipmentException If the given item is not equipment.
   */
  public void unequip(Item item)
    throws NotEquipmentException, NoSuchItemException
  {
    if (item.get("slot") == null) {
      throw new NotEquipmentException("Given item was not equipment");
    }

    String slot = item.get("slot");
    if (item != equipment.get(slot)) {
      throw new NoSuchItemException("Player does not have the given item");
    }

    equipment.remove(slot);
    addItem(item);
  }

  /**
   * Returns the equipment at the given slot.
   * @param slot Slot for which to retrive the item.
   * @return The item at the given equipment slot.
   */
  public Item getEquipment(String slot) {
    return equipment.get(slot);
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

  /**
   * @return XML representation of the character.
   */
  public String getXML() {
    StringBuffer b = new StringBuffer();

    b.append(String.format("<character name=\"%s\" ", name));

    b.append(String.format(
      "hp=\"%d\" maxhp=\"%d\" mp=\"%d\" maxmp=\"%d\" sp=\"%d\" maxsp=\"%d\" ",
      hp, maxHp, mp, maxMp, sp, maxSp
    ));

    b.append(String.format(
      "strength=\"%d\" vitality=\"%d\" magic=\"%d\" speed=\"%d\" gold=\"%d\">",
      strength, vitality, magic, speed, gold
    ));

    // Game location
    if (room != null) {
      b.append(String.format(
        "<location area=\"%s\" room=\"%s\" />",
        room.getArea().getId(),
        room.getId()
      ));
    }

    // Inventory
    b.append("<inventory>");
    for (Item i : inventory) {
      b.append(i.getXML());
    }
    b.append("</inventory>");

    // Equipment
    b.append("<equipment>");
    for (String slot : equipment.keySet()) {
      b.append(equipment.get(slot).getXML());
    }
    b.append("</equipment>");

    b.append("</character>");
    return b.toString();
  }
}
