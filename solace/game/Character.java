package solace.game;

import java.util.*;

import solace.net.Connection;
import solace.util.EventEmitter;
import solace.util.EventListener;
import solace.util.Log;
import solace.util.SkillNotFoundException;
import solace.util.Skills;

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
  Account account = null;
  String id = null;
  String name;
  String description;
  Room room = null;
  EventEmitter events;

  int level;
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
  String majorStat = "none";
  String minorStat = "none";

  long gold;
  List<Item> inventory = null;
  Hashtable<String, Item> equipment = new Hashtable<String, Item>();
  TreeSet<String> skillIds = new TreeSet<String>();
  List<Skill> skills = new ArrayList<Skill>();

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

  /**
   * Adds a skill to the character with the given id and level.
   * @param id Id of the skill to add.
   * @param level Level of the skill.
   * @throws SkillNotFoundException If there is no skill with the given id.
   */
  public void addSkill(String id, int level)
    throws SkillNotFoundException
  {
    if (skillIds.contains(id)) {
      return;
    }
    Skill skill = Skills.cloneSkill(id);
    skill.setLevel(level);
    skillIds.add(id);
    skills.add(skill);
  }

  /**
   * @return The character's skills.
   */
  public Collection<Skill> getSkills() { return skills; };

  /**
   * Sets the major statistic for the character. Major statistics grow the
   * the fastest of all stats as a character progresses in level.
   * @param name Name of the major stat.
   */
  public void setMajorStat(String name) {
    majorStat = name;
  }

  /**
   * Sets the minor stat for the character. Minor stats grow at a medium pace
   * as a character levels.
   * @param name [description]
   */
  public void setMinorStat(String name) {
    minorStat = name;
  }

  /**
   * @return The name of the character's major stat.
   */
  public String getMajorStat() {
    return majorStat;
  }

  /**
   * @return THe name of the character's minor stat.
   */
  public String getMinorStat() {
    return minorStat;
  }

  /**
   * Generates the character's stats based on level, given power, and focused
   * stats.
   * @param power Power level for the character.
   */
  public void generateStats(int power) {
    // Power scale is 1 - 100:
    double powerMod = getPowerModifier(power);
    setStrength((int)((double)generateStat("strength") * (1.0 + powerMod)));
    setVitality((int)((double)generateStat("vitality") * (1.0 + powerMod)));
    setMagic((int)((double)generateStat("magic") * (1.0 + powerMod)));
    setSpeed((int)((double)generateStat("speed") * (1.0 + powerMod)));

    Log.trace(String.format(
      "Stats generated: %d (str), %d (vit), %d (mag), %d (spe)",
      strength, vitality, magic, speed
    ));

    // Set resources based on stats
    // TODO Tweak these for lower levels (stays at minimums until level 10...)
    hp = maxHp = (int)(
      0.2 * (0.70 * getVitality() + 0.15 * getStrength() + 0.15 * getSpeed()) * level
    );
    mp = maxMp = (int)(
      0.2 * (0.9 * getMagic() + 0.1 * getVitality()) * level
    );
    sp = maxSp = (int)(
      0.2 * (0.5 * getVitality() + 0.5 * getSpeed()) * level
    );

    hp = Math.max(hp, 10);
    mp = Math.max(mp, 10);
    sp = Math.max(sp, 10);

    Log.trace(String.format(
      "Resources generated: %d (hp), %d (mp), %d (sp)",
      hp, mp, sp
    ));
  }

  /**
   * Generates a stat for the character.
   *
   * Base Stat Progression: Levels 1 - 100
   *         1.0x     0.65x   0.33..x
   * Level   major    minor   tertiary
   * 1       10       6       3
   * 10      25       15      8
   * 20      75       50      25
   * 30      150      100     50
   * 40      250      163     83
   * 50      400      260     133
   * 60      550      358     183
   * 70      700      455     233
   * 80      800      520     267
   * 90      900      585     300
   * 100     1000     650     333
   *
   * @param name Name of the stat being generated.
   * @return A level appropriate stat.
   */
  public int generateStat(String name) {
    int major = 10;

    if (level >= 1 && level < 10) {
      major = 10 + (int)(15.0 * (double)(level-1) / 10.0);
    }
    else if (level < 20) {
      major = 25 + (int)(50.0 * (double)(level-10) / 10.0);
    }
    else if (level < 30) {
      major = 75 + (int)(75.0 * (double)(level-20) / 10.0);
    }
    else if (level < 40) {
      major = 150 + (int)(100.0 * (double)(level-30) / 10.0);
    }
    else if (level < 50) {
      major = 250 + (int)(150.0 * (double)(level-40) / 10.0);
    }
    else if (level < 60) {
      major = 400 + (int)(150.0 * (double)(level-50) / 10.0);
    }
    else if (level < 70) {
      major = 550 + (int)(150.0 * (double)(level-60) / 10.0);
    }
    else if (level < 80) {
      major = 700 + (int)(100.0 * (double)(level-70) / 10.0);
    }
    else if (level < 90) {
      major = 800 + (int)(100.0 * (double)(level-80) / 10.0);
    }
    else if (level < 100) {
      major = 900 + (int)(100.0 * (double)(level-90) / 10.0);
    }
    else if (level >= 100) {
      major = 1000;
    }

    if (majorStat.equals(name)) {
      return major;
    }
    if (minorStat.equals(name)) {
      return (int)(0.65 * major);
    }
    return major / 3;
  }

  /**
   * Generates power modifier based on the given power scale. Power modifiers
   * are used when generating stats for mobiles to add variation to the mix
   * making some mobiles stronger and some weaker at the same level.
   *
   * Below is the power modifier table mapping power levesl to modifiers:
   *
   * Power    Title         Modifier
   * --------------------------------
   * 1        weak          -0.25
   * 25       average        0.00
   * 50       strong        +0.15
   * 75       boss          +0.30
   * 100      legendary     +0.50
   *
   * @param power Level of the power on a scale of 1 to 100.
   */
 public double getPowerModifier(int power) {
   // Ensure the levels is on the scale
   if (power < 1) { power = 1; }
   if (power > 100) { power = 100; }

   // Calculate the power modifier
   if (power >= 1 && power < 25) {
     return -0.25 + 0.25 * (double)power / 25.0;
   }
   if (power < 50) {
     return 0.15*((double)power - 25.0) / 25.0;
   }
   if (power < 75) {
     return 0.15 + 0.15*((double)power - 50) / 25.0;
   }
   return 0.3 + 0.2*((double)power - 75) / 25.0;
 }


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

  // Statistic accessors and mutators
  // TODO Document me
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

    // Skills
    b.append("<skills>");
    for (Skill s : skills) {
      b.append(String.format(
        "<skill id=\"%s\" level=\"%d\" />",
        s.getId(),
        s.getLevel()
      ));
    }
    b.append("</skills>");

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
