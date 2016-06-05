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
public class Character implements Player {
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
  PlayState state = PlayState.STANDING;

  String name;
  String description;

  int level;
  int hp;
  int mp;
  int sp;

  String majorStat = "none";
  String minorStat = "none";

  long gold;
  List<Item> inventory = null;
  Hashtable<String, Item> equipment = new Hashtable<String, Item>();
  HashSet<String> skillIds = new HashSet<String>();
  List<Skill> skills = new ArrayList<Skill>();

  Room room = null;

  Account account = null;
  String prompt = Character.DEFAULT_PROMPT;

  boolean onGCDCooldown = false;

  /**
   * Creates a new character.
   * @param n Name for the character;
   */
  public Character(String n) {
    name = n;
    level = 1;
    inventory = Collections.synchronizedList(new ArrayList<Item>());
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
   * Gets an ability stat for the character.
   * @param name Name of the stat being generated.
   * @return A level appropriate stat.
   * @see solace.game.Stats
   */
  protected int getAbility(String name) {
    int ability = Stats.getAbility(this, name);
    return ability + getModFromEquipment(name);
  }

  /**
   * Gets the saving throw with the given name.
   * @param name Name of the saving throw.
   * @return The saving throw.
   * @see solace.game.Stats
   */
  protected int getSavingThrow(String name) {
    try {
      return Stats.getSavingThrow(this, name) + getModFromEquipment(name);
    }
    catch (InvalidSavingThrowException iste) {
      Log.error(String.format(
        "Invalid saving throw name encountered: %s", name
      ));
    }
    return 0;
  }

  /**
   * @see solace.game.Player
   */
  public PlayState getPlayState() {
    return state;
  }

  /**
   * @see solace.game.Player
   */
  public void setPlayState(PlayState s) {
    state = s;
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
   * @return The character's level.
   */
  public int getLevel() { return level; }

  /**
   * Set the character's level.
   * @param l Level to set for the character.
   */
  public void setLevel(int l) { level = l; }

  /**
   * Sets the major statistic for the character. Major statistics grow the
   * the fastest of all stats as a character progresses in level.
   * @param name Name of the major stat.
   */
  public void setMajorStat(String name) { majorStat = name; }

  /**
   * Sets the minor stat for the character. Minor stats grow at a medium pace
   * as a character levels.
   * @param name [description]
   */
  public void setMinorStat(String name) { minorStat = name; }

  /**
   * @return The name of the character's major stat.
   */
  public String getMajorStat() { return majorStat; }

  /**
   * @return THe name of the character's minor stat.
   */
  public String getMinorStat() { return minorStat; }

  /**
   * @return The character's armor class.
   */
  public int getAC() {
    return Stats.getAC(this) + getModFromEquipment("ac");
  }

  /**
   * @return The character's strength ability score.
   */
  public int getStrength() { return getAbility("strength"); }

  /**
   * @return The character's vitality ability score.
   */
  public int getVitality() { return getAbility("vitality"); }

  /**
   * @return The character's magic ability score.
   */
  public int getMagic() { return getAbility("magic"); }

  /**
   * @return The character's speed ability score.
   */
  public int getSpeed() { return getAbility("speed"); }

  /**
   * @return The player's current hit points.
   */
  public int getHp() { return hp; }

  /**
   * Sets the player's current hit points.
   * @param v HP to set.
   */
  public void setHp(int v) { hp = v; }

  /**
   * @return The player's maximum hit points.
   * @see solace.game.Stats
   */
  public int getMaxHp() {
    return Stats.getMaxHp(this) + getModFromEquipment("hp");
  }

  /**
   * @return The player's current mp.
   */
  public int getMp() { return mp; }

  /**
   * Sets the player's current mp.
   * @param v MP to set.
   */
  public void setMp(int v) { mp = v; }

  /**
   * @return The player's maximum MP.
   * @see solace.game.Stats
   */
  public int getMaxMp() {
    return Stats.getMaxMp(this) + getModFromEquipment("mp");
  }

  /**
   * @return The character's current sp.
   */
  public int getSp() { return sp; }

  /**
   * Sets the character's current sp.
   * @param v SP to set.
   */
  public void setSp(int v) { sp = v; }

  /**
   * @return The character's maximum sp.
   * @see soalce.game.Stats
   */
  public int getMaxSp() {
    return Stats.getMaxSp(this) + getModFromEquipment("sp");
  }

  /**
   * @return The character's will saving throw.
   */
  public int getWillSave() { return getSavingThrow("will"); }

  /**
   * @return The character's reflex saving throw.
   */
  public int getReflexSave() { return getSavingThrow("reflex"); }

  /**
   * @return The character's resolve saving throw.
   */
  public int getResolveSave() { return getSavingThrow("resolve"); }

  /**
   * @return The character's vigor saving throw.
   */
  public int getVigorSave() { return getSavingThrow("vigor"); }

  /**
   * @return The character's prudence saving throw.
   */
  public int getPrudenceSave() { return getSavingThrow("prudence"); }

  /**
   * @return The character's guile saving throw.
   */
  public int getGuileSave() { return getSavingThrow("guile"); }

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
   * @return the hit modifier for the character.
   * @see solace.game.Stats
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
   * @return the damage modifier for the character.
   * @see solace.game.Stats
   */
  public int getDamageMod() {
    return Stats.getDamageMod(this) + getModFromEquipment("damage");
  }

  /**
   * @return The number of attacks for the character.
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
  public int applyDamage(int damage) {
    hp -= damage;
    return damage;
  }

  /**
   * @see solace.game.Player
   */
  public boolean isDead() { return getHp() <= 0; }

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
  public void removeGold(long g)
    throws CurrencyException
  {
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

    b.append(String.format(
      "<character name=\"%s\" level=\"%d\" hp=\"%d\" mp=\"%d\" sp=\"%d\" gold=\"%d\" " +
      "major-stat=\"%s\" minor-stat=\"%s\" play-state=\"%s\" prompt=\"%s\">",
      name, level, hp, mp, sp, gold, majorStat, minorStat, state.toString(), prompt
    ));

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

    b.append("</character>");
    return b.toString();
  }

  /**
   * @return `true` if the player is sleeping, `false` otherwise.
   */
  public boolean isSleeping() {
    return state == PlayState.SLEEPING;
  }

  /**
   * Sets the character to be in the sleeping play state.
   */
  public void setSleeping() {
    state = PlayState.SLEEPING;
  }

  /**
   * @return `true` if the player is resting, `false` otherwise.
   */
  public boolean isResting() {
    return state == PlayState.RESTING;
  }

  /**
   * Sets the character to be in the resting play state.
   */
  public void setResting() {
    state = PlayState.RESTING;
  }

  /**
   * @return `true` if the player is sitting, `false` otherwise.
   */
  public boolean isSitting() {
    return state == PlayState.SITTING;
  }

  /**
   * Sets the character to be in the sitting play state.
   */
  public void setSitting() {
    state = PlayState.SITTING;
  }

  /**
   * @return `true` if the player is resting or sitting, `false` otherwise.
   */
  public boolean isRestingOrSitting() {
    return state == PlayState.RESTING || state == PlayState.SITTING;
  }

  /**
   * @return `true` if the player is standing, `false` otherwise.
   */
  public boolean isStanding() {
    return state == PlayState.STANDING;
  }

  /**
   * Sets the character to be in the standing play state.
   */
  public void setStanding() {
    state = PlayState.STANDING;
  }

  /**
   * @return `true` if the player is fighting, `false` otherwise.
   */
  public boolean isFighting() {
    return state == PlayState.FIGHTING;
  }

  /**
   * Sets the character to be in the fighting play state.
   */
  public void setFighting() {
    state = PlayState.FIGHTING;
  }

  /**
   * @return `true` if the character is standing or fighting, `false` otherwise.
   */
  public boolean isStandingOrFighting() {
    return state == PlayState.FIGHTING || state == PlayState.STANDING;
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
   * Determines if the player has a passive of the given name.
   * @param  name Name of the passive.
   * @return      True if they have the passive, false otherwise.
   */
  public boolean hasPassive(String name) {
    for (Skill skill : skills) {
      if (skill.grantsPassive(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the skill level of the skill with the maximum level that grants the
   * given passive. This can occur if two skills both grant the same passive.
   * @param  name Name of the passive.
   * @return      The skill level of the skill that grants the passive or -1 if
   *              no skill grants the passive.
   */
  public int getMaximumSkillLevelForPassive(String name) {
    int maximum = -1;
    for (Skill skill : skills) {
      if (skill.grantsPassive(name)) {
        int level = skill.getLevel();
        if (level > maximum) {
          maximum = level;
        }
      }
    }
    return maximum;
  }

  /**
   * Determines if the character successfully parries an attack.
   * @return True if the attack is parried, false otherwise.
   */
  public boolean parry() {
    int skillLevel = getMaximumSkillLevelForPassive("parry");
    if (skillLevel < 1) {
      return false;
    }
    double chance = 0.05 + 0.1 * ((double)skillLevel / 100.0);
    return Roll.uniform() < chance;
  }

  /**
   * @see solace.game.Player
   */
  public int getCooldownSkillLevel(String name) {
    int maximum = -1;
    for (Skill skill : skills) {
      if (skill.grantsCooldown(name)) {
        int level = skill.getLevel();
        if (level > maximum) {
          maximum = level;
        }
      }
    }
    return maximum;
  }

  /**
   * @see solace.game.Player
   */
  public boolean isOnGCD() {
    return onGCDCooldown;
  }

  /**
   * @see solace.game.Player
   */
  public void setOnGCD() {
    onGCDCooldown = true;
    // TODO GCD cooldowns should really be independent of the global clock...
    Clock.getInstance().schedule(
      String.format("GCD for %s", getName()),
      2,
      new Runnable() { public void run() { onGCDCooldown = false; } });
  }
}
