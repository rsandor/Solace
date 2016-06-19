package solace.game;

import java.util.*;

import solace.net.Connection;
import solace.util.*;
import solace.xml.GameParser;
import solace.cmd.GameException;

/**
 * Represents a player character in the game world.
 * @author Ryan Sandor Richards.
 */
public class Character extends AbstractPlayer {
  /**
   * Unmodifiable collection of all valid equipment slots for a given character.
   */
  public static final Collection<String> EQ_SLOTS =
    Collections.unmodifiableCollection(GameParser.parseEquipment());

  /**
   * Default prompt given to new characters.
   */
  public static final String DEFAULT_PROMPT =
    "(( {G%h{x/{g%H{xhp {M%m{x/{m%M{xmp {Y%s{x/{y%S{xsp )) {Y%gg{x %T>";

  /**
   * Determines if the given slot name is a valid equipment slot.
   * @param name Name of the slot to check.
   * @return `true` if the slot is valid, `false` otherwise.
   */
  public static boolean isValidEquipmentSlot(String name) {
    return EQ_SLOTS.contains(name);
  }

  String id = null;
  String name;
  String description;
  long gold;
  List<Item> inventory = null;
  Hashtable<String, Item> equipment = new Hashtable<String, Item>();
  HashSet<String> skillIds = new HashSet<String>();
  List<Skill> skills = new ArrayList<Skill>();
  Account account = null;
  String prompt = Character.DEFAULT_PROMPT;
  Hashtable<String, String> hotbar = new Hashtable<String, String>();
  Race race;

  /**
   * Creates a new character.
   * @param n Name for the character;
   */
  public Character(String n) {
    super();
    name = n;
    level = 1;
    inventory = Collections.synchronizedList(new ArrayList<Item>());
  }

  /**
   * Sets the passives and cooldowns this character has based on the character's
   * core skills, role skills, and race.
   */
  public void setPassivesAndCooldowns() {
    super.setPassivesAndCooldowns();

    // Skill based passives and cooldowns
    for (Skill skill : skills) {
      int skillLevel = skill.getLevel();
      for (String passive : skill.getPassives()) {
        if (skillLevel > getPassiveLevel(passive)) {
          setPassive(passive, skillLevel);
        }
      }
      for (String cooldown : skill.getCooldowns()) {
        if (skillLevel > getCooldownLevel(cooldown)) {
          setCooldown(cooldown, skillLevel);
        }
      }
    }

    // Racial passives and cooldowns
    // NOTE Racial passives and cooldowns always have a skill level of 100.
    for (String passive : race.getPassives()) {
      setPassive(passive, 100);
    }

    for (String cooldown : race.getCooldowns()) {
      setCooldown(cooldown, 100);
    }
  }

  /**
   * Tallies the total modifier of the given name for the character granted by
   * their current equipment.
   * @param name Name of the modifier to tally.
   * @return The total modifier of the given name granted by the equipment.
   */
  protected int getModFromEquipment(String name) {
    int stat = 0;
    for (Item item : equipment.values()) {
      try {
        String mod = item.get(name);
        if (mod != null) {
          stat += Integer.parseInt(mod);
        }
      }
      catch (NumberFormatException nfe) {
        Log.warn(String.format(
          "Item %s equipped on character %s has non-integer value for %s",
          item.get("description.inventory"),
          getName(),
          name
        ));
      }
    }
    return stat;
  }

  /**
   * Gets an ability stat for the character with equipment bonuses.
   * @param name Name of the stat being generated.
   * @return A level appropriate stat.
   * @see solace.game.AbstractPlayer
   */
  protected int getAbility(String name) {
    return super.getAbility(name) + getModFromEquipment(name);
  }

  /**
   * Gets the saving throw with the given name and adds any bonuses due to
   * character equipment.
   * @param name Name of the saving throw.
   * @return The saving throw.
   * @see solace.game.AbstractPlayer
   */
  protected int getSavingThrow(String name) {
    return super.getSavingThrow(name) + getModFromEquipment(name);
  }

  /**
   * Gets the maximum value for the given resource and adds any bonuses due to
   * character equipment.
   * @param name Name of the resource.
   * @return The maximum value of the resource for this player, or -1 if the
   *   provided resource name is invalid.
   */
  protected int getMaxResource(String name) {
    return super.getMaxResource(name) + getModFromEquipment(name);
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
   * @see solace.game.Player
   */
  public String getName() { return name; }

  /**
   * @param n Name to set for the character.
   */
  public void setName(String n) { name = n; }

  /**
   * @see solace.game.Player
   */
  public String getDescription() { return description; }

  /**
   * @param n Description to set for the character.
   */
  public void setDescription(String n) { description = n; }

  /**
   * @see solace.game.Player
   */
  public int getAC() {
    return super.getAC() + getModFromEquipment("ac");
  }

  /**
   * @see solace.game.Player
   */
  public int getAttackRoll() {
    Item weapon = getEquipment("weapon");
    // TODO Need better unarmed calculations here
    if (weapon == null) {
      return (int)(level * 1.5);
    }
    return Stats.getWeaponAttackRoll(weapon.getInt("level"));
  }

  /**
   * @see solace.game.Player
   */
  public int getHitMod() {
    return Stats.getHitMod(this) + getModFromEquipment("hit");
  }

  /**
   * @see solace.game.Player
   */
  public int getAverageDamage() {
    Item weapon = getEquipment("weapon");
    if (weapon == null) {
      return (int)(level * 1.5 + 1);
    }
    return Stats.getWeaponAverageDamage(weapon.getInt("level"));
  }

  /**
   * @see solace.game.Player
   */
  public int getDamageMod() {
    return Stats.getDamageMod(this) + getModFromEquipment("damage");
  }

  /**
   * @see solace.game.Player
   */
  public int getNumberOfAttacks() {
    int attacks = 1;
    if (hasPassive("second attack")) attacks++;
    if (hasPassive("third attack")) attacks++;
    return attacks;
  }

  /**
   * @see solace.game.Player
   */
  public void die(Player killer) {
    try {
      setPlayState(PlayState.DEAD);
      hp = 1;
      mp = 0;
      sp = 0;

      if (killer != null) {
        sendMessage(String.format(
          "You have been {Rkilled{x by %s!", killer.getName()));
      } else {
        sendMessage("\n\rYou have {Rdied{x!\n\r");
      }

      Room origin = getRoom();
      Room destination = World.getDefaultRoom();

      origin.getCharacters().remove(this);

      if (killer != null) {
        Player[] excludes = { this, killer };
        origin.sendMessage(
          String.format("%s has {Rkilled{x %s!", killer.getName(), getName()),
          excludes);
      } else {
        origin.sendMessage(String.format("%s has died!", getName()));
      }

      destination.sendMessage(String.format(
        "A bright light flashes and %s reconstitues here battered and bruised.",
        getName()
      ));
      destination.getCharacters().add(this);

      setPlayState(PlayState.RESTING);
      setRoom(destination);
      sendMessage(room.describeTo(this));
    }
    catch (GameException ge) {
      Log.error("World configuration does not define a default room!");
    }
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
   * @return An unmodifiable collection of the character's skills.
   */
  public Collection<Skill> getSkills() {
    return Collections.unmodifiableCollection(skills);
  };

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
   * Removes a given amount of gold from the character. Does nothing if the
   * character doesn't have enough gold.
   * @param g Gold to remove from the character.
   * @throws CurrencyException If the player has less gold than what was given.
   */
  public void removeGold(long g) throws CurrencyException {
    if (gold < g) {
      throw new CurrencyException(String.format(
        "Unable to remove %d gold from %s.", g, name
      ));
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
  public Item findItem(String name) {
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
  public Item equip(Item item) throws NotEquipmentException {
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
   * @see solace.game.Player
   */
  public boolean isMobile() {
    return false;
  }

  /**
   * Helper method to send messages to a character. This also resends their
   * prompt after the message has been sent.
   * @param msg Message to send.
   */
  public void sendMessage(String msg) {
    Connection c = getConnection();
    c.sendln("\n" + msg);
    c.send(c.getStateController().getPrompt());
  }

  /**
   * Sends a string to the character.
   * @param msg Message to send the character.
   */
  public void send(String msg) {
    getConnection().send(msg);
  }

  /**
   * Sends a string to the character append with a newline.
   * @param msg Message to send the character.
   */
  public void sendln(String msg) {
    getConnection().sendln(msg);
  }

  /**
   * Sends a string to the character wrapped with newlines.
   * @param msg Message to send the character.
   */
  public void wrapln(String msg) {
    getConnection().wrapln(msg);
  }

  /**
   * @return XML representation of the character.
   */
  public String getXML() {
    StringBuffer b = new StringBuffer();

    b.append("<character ");

    b.append(String.format(
      "name=\"%s\" level=\"%d\" race=\"%s\" ",
      name, level, race.getName()
    ));

    b.append(String.format(
      "hp=\"%d\" mp=\"%d\" sp=\"%d\" gold=\"%d\" ",
      hp, mp, sp, gold
    ));

    b.append(String.format(
      "major-stat=\"%s\" minor-stat=\"%s\" play-state=\"%s\" prompt=\"%s\"",
      majorStat, minorStat, state.toString(), prompt
    ));

    b.append(">");

    // Game location
    if (room != null) {
      b.append(String.format(
        "<location area=\"%s\" room=\"%s\" />",
        room.getArea().getId(),
        room.getId()
      ));
    }

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

    // Hotbar
    b.append("<hotbar>");
    for (String key : hotbar.keySet()) {
      b.append(String.format(
        "<entry key=\"%s\" command=\"%s\" />", key, hotbar.get(key)));
    }
    b.append("</hotbar>");

    b.append("</character>");
    return b.toString();
  }

  /**
   * Determines whether or not this player has a name with the given prefix.
   * @param  namePrefix Prefix by which to test.
   * @return `true` if the player has a name with the given prefix.
   *  `false` otherwise.
   */
  public boolean hasName(String namePrefix) {
    String[] names = getName().split("\\s+");
    for (String n : names) {
      if (n.toLowerCase().startsWith(namePrefix.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return The character's prompt format.
   */
  public String getPrompt() {
    return prompt;
  }

  /**
   * Sets the character's prompt format.
   * @param p The prompt format for the character.
   */
  public void setPrompt(String p) {
    prompt = p;
  }

  /**
   * Determines if a player has a skill with the given name.
   * @param  name Name of the skill.
   * @return      `true` if they have the skill, `false` otherwise.
   */
  public boolean hasSkill(String name) {
    return skillIds.contains(name);
  }

  /**
   * Retrieves a hotbar command for the given key.
   * @param key Key for the command to get.
   * @return The hotbar command.
   */
  public String getHotbarCommand(String key) {
    return hotbar.get(key);
  }

  /**
   * Sets a hotbar command for the given key.
   * @param key Key for the hotbar command.
   * @param command The command to set.
   */
  public void setHotbarCommand(String key, String command) {
    hotbar.put(key, command);
  }

  /**
   * @return The race for the character.
   */
  public Race getRace() { return race; }

  /**
   * Sets the race for the character.
   * @param r Race to set.
   */
  public void setRace(Race r) {
    race = r;
    setPassivesAndCooldowns();
  }
}
