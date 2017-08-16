package solace.game;

import java.util.*;

import com.google.common.base.Joiner;
import solace.io.*;
import solace.net.Connection;
import solace.util.*;
import solace.io.xml.GameParser;
import solace.cmd.GameException;

/**
 * Represents a player character in the game world.
 * @author Ryan Sandor Richards.
 */
public class Character extends AbstractPlayer {
  /**
   * Unmodifiable collection of all valid equipment slots for a given character.
   */
  @SuppressWarnings("WeakerAccess")
  public static final Collection<String> EQ_SLOTS =
    Collections.unmodifiableCollection(GameParser.parseEquipment());

  /**
   * Character returned when instead of null when a character could not be found.
   */
  static final Character NULL = new Character();

  /**
   * Determines if the given slot name is a valid equipment slot.
   * @param name Name of the slot to check.
   * @return `true` if the slot is valid, `false` otherwise.
   */
  static boolean isValidEquipmentSlot(String name) {
    return EQ_SLOTS.contains(name);
  }

  String id = "";
  String name = "";
  private String description = "";
  private long gold = 0;
  private final List<Item> inventory = Collections.synchronizedList(new ArrayList<Item>());
  private Hashtable<String, Item> equipment = new Hashtable<>();
  private Hashtable<String, Skill> skills = new Hashtable<>();

  private Account account = Account.NULL;
  private String prompt = Config.get("game.default.prompt");
  private Hashtable<String, String> hotbar = new Hashtable<>();
  private Race race = Race.NULL;

  /**
   * Creates a null character.
   */
  public Character() {
    super();
    id = "null";
    name = "null character";
  }

  /**
   * Creates a new character.
   * @param n Name for the character;
   */
  public Character(String n) {
    super();
    name = n;
    level = 1;
  }

  @Override
  public void setPassivesAndCooldowns() {
    super.setPassivesAndCooldowns();

    // Skill based passives and cooldowns
    for (Skill skill : skills.values()) {
      int skillLevel = skill.getLevel();
      for (String passive : skill.getPassives()) {
        setPassive(passive, skillLevel);
      }
      for (String cooldown : skill.getCooldowns()) {
        setCooldown(cooldown, skillLevel);
      }
    }

    // Racial passives and cooldowns
    for (String passive : race.getPassives()) {
      setPassive(passive, getLevel());
    }

    for (String cooldown : race.getCooldowns()) {
      setCooldown(cooldown, getLevel());
    }
  }

  /**
   * Tallies the total modifier of the given name for the character granted by
   * their current equipment.
   * @param name Name of the modifier to tally.
   * @return The total modifier of the given name granted by the equipment.
   */
  private int getModFromEquipment(String name) {
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
  public int getSavingThrow(String name) {
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

    // Unarmed attacks
    if (weapon == null) {
      if (hasSkill("unarmed")) {
        return Stats.getWeaponAttackRoll(getLevel(), skills.get("unarmed").getLevel());
      }
      return (int)(level * 1.5);
    }

    // Weapon attacks
    String prof = weapon.get("proficiency");
    if (prof == null) {
      Log.warn(String.format("Encountered weapon without proficiency on player '%s'.", getName()));
      prof = "";
    }
    return Stats.getWeaponAttackRoll(weapon.getInt("level"), getWeaponProficiency(prof));
  }

  /**
   * @see solace.game.Player
   */
  public int getHitMod() { return Stats.getHitMod(this) + getModFromEquipment("hit"); }

  /**
   * @see solace.game.Player
   */
  public int getAverageDamage() {
    Item weapon = getEquipment("weapon");
    if (weapon == null) {
      if (hasSkill("unarmed")) {
        return Stats.getUnarmedAverageDamage(getLevel());
      }
      return (int)(level * 1.5);
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
      removeAllBuffs();

      hp = 1;
      mp = 0;
      sp = 0;

      if (killer != null) {
        sendMessage(String.format(
          "You have been {R}killed{x} by %s!", killer.getName()));
      } else {
        sendMessage("\n\rYou have {R}died{x}!\n\r");
      }

      Room origin = getRoom();
      Room destination = Areas.getInstance().getDefaultRoom();
      if (destination == null) {
        throw new GameException("Default room not defined.");
      }

      origin.getPlayers().remove(this);

      if (killer != null) {
        Player[] excludes = { this, killer };
        origin.sendMessage(
          String.format("%s has {R}killed{x} %s!", killer.getName(), getName()),
          excludes);
      } else {
        origin.sendMessage(String.format("%s has died!", getName()));
      }

      destination.sendMessage(String.format(
        "A bright light flashes and %s reconstitues here battered and bruised.",
        getName()
      ));
      destination.getPlayers().add(this);

      setPlayState(PlayState.RESTING);
      setRoom(destination);
      sendMessage(room.describeTo(this));
    } catch (GameException ge) {
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
    if (skills.keySet().contains(id)) {
      Log.warn(String.format(
        "Duplicate skill '%s' encountered for player '%s', skipping",
        id, getName()));
      return;
    }
    Log.debug(String.format("Adding skill: '%s' at level %d to player '%s", id, level, getName()));
    Skill skill = Skills.getInstance().cloneSkill(id);
    skill.setLevel(level);
    skills.put(id, skill);
  }

  /**
   * @return An unmodifiable collection of the character's skills.
   */
  public Collection<Skill> getSkills() {
    return skills.values();
  }

  /**
   * Reset each skill on the character.
   */
  public void resetSkills() {
    Hashtable<String, Integer> skillToLevel = new Hashtable<>();
    skills.values().forEach(s -> skillToLevel.put(s.getId(), s.getLevel()));
    skills.clear();
    skillToLevel.keySet().forEach(id -> {
      try {
        addSkill(id, skillToLevel.get(id));
      } catch (SkillNotFoundException e) {
        Log.warn(String.format(
          "Unknown skill id '%s' encountered when reloading skills for '%s', skipping.",
          id, getName()));
      }
    });
    setPassivesAndCooldowns();
  }

  /**
   * Resets the race for the character.
   */
  public void resetRace() {
    setRace(Races.getInstance().get(race.getName()));
  }

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
   */
  public void addItem(Item item) {
    // TODO: Currently no limits on number of items / weight; need to add this when the time comes.
    inventory.add(item);
  }

  /**
   * Removes an item from the player's inventory. Returns false if the item
   * cannot be removed from the player's inventory (e.g. when it is cursed,
   * etc.).
   * @param item Item to remove.
   */
  public void removeItem(Item item) {
    // TODO: No limits on item removal yet, implement them when applicable
    inventory.remove(item);
  }

  /**
   * Equips a particular item onto the character. If a piece of equipment
   * already inhabits the particular slot it is removed and returned to the
   * character's inventory.
   * @param item Item to equip.
   * @return The item that was previously equipped.
   */
  @SuppressWarnings("UnusedReturnValue")
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
  @SuppressWarnings("unused")
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
  @SuppressWarnings("WeakerAccess")
  public Item getEquipment(
    @SuppressWarnings("SameParameterValue") String slot
  ) {
    return equipment.get(slot);
  }

  /**
   * @see solace.game.Player
   */
  public Connection getConnection() {
    if (account != null) {
      return Game.connectionFromAccount(account);
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
  @Override
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
   * @see solace.game.Player
   */
  public solace.game.Character getCharacter() {
    return this;
  }

  /**
   * Helper method to send messages to a character. This also resends their
   * prompt after the message has been sent.
   * @param msg Messages to send.
   */
  public void sendMessage(String msg) {
    Connection c = getConnection();
    c.sendln("\n" + msg);
    c.send(c.getStateController().getPrompt());
  }

  /**
   * Sends a string to the character.
   * @param msg Messages to send the character.
   */
  public void send(String msg) {
    getConnection().send(msg);
  }

  /**
   * Sends a string to the character append with a newline.
   * @param msg Messages to send the character.
   */
  public void sendln(String msg) {
    getConnection().sendln(msg);
  }

  /**
   * @see solace.game.Player;
   */
  public void sendln(String... lines) { sendln(Joiner.on("\n\r").join(lines)); }

  /**
   * Sends a string to the character wrapped with newlines.
   * @param msg Messages to send the character.
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
      "name=\"%s\" level=\"%d\" ",
      name, level
    ));

    if (race != null) {
      b.append(String.format("race=\"%s\" ", race.getName()));
    } else {
      Log.error(String.format("Null race encountered for '%s'", getName()));
      b.append("race=\"human\" ");
    }

    b.append(String.format(
      "hp=\"%d\" mp=\"%d\" sp=\"%d\" gold=\"%d\" ",
      hp, mp, sp, gold
    ));

    b.append(String.format(
      "major-stat=\"%s\" minor-stat=\"%s\" play-state=\"%s\" prompt=\"%s\" ",
      majorStat, minorStat, state.toString(), prompt
    ));

    b.append(String.format(
      "immortal=\"%s\"",
      isImmortal() ? "true" : "false"
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
    skills.values().forEach(s -> b.append(String.format("<skill id=\"%s\" level=\"%d\" />", s.getId(),  s.getLevel())));
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
   * @param id Name of the skill.
   * @return `true` if they have the skill, `false` otherwise.
   */
  @SuppressWarnings({"unused", "WeakerAccess"})
  public boolean hasSkill(String id) { return skills.keySet().contains(id); }

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

  @Override
  public int getWeaponProficiency(String name) {
    if (!WeaponProficiencies.getInstance().has(name)) {
      Log.warn(String.format("Character.getWeaponProficiency encountered unknown name: %s", name));
      return 0;
    }
    WeaponProficiency prof = WeaponProficiencies.getInstance().get(name);
    int skillLevel = hasSkill(prof.getSkill()) ? skills.get(prof.getSkill()).getLevel() : 0;
    if (prof.isSimple()) {
      return Math.max(Math.min(75, getLevel()), skillLevel);
    }
    return skillLevel;
  }

  @Override
  public Set<DamageType> getBaseAttackDamageTypes() {
    Item weapon = getEquipment("weapon");
    if (weapon != null) {
      return weapon.getDamageTypes();
    }
    Set<DamageType> types = new HashSet<>();
    try {
      types.add(DamageTypes.getInstance().get("bludgeoning"));
    } catch (AssetNotFoundException e) {
      Log.warn("Missing 'bludgeoning' damage type for unarmed.");
    }
    return types;
  }
}
