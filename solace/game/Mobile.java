package solace.game;

import com.google.common.base.Joiner;
import solace.util.*;
import solace.util.EventEmitter;
import solace.util.EventListener;

/**
 * Represents a mobile in the game world.
 * @author Ryan Sandor Richards
 */
public class Mobile extends AbstractPlayer {
  private int power;
  private boolean isPlaced = false;
  private EventEmitter events;
  private Template template;

  /**
   * Creates a new mobile.
   */
  public Mobile(Template t) {
    super();
    template = t;
    events = new EventEmitter();
  }

  /**
   * @see solace.game.Player
   */
  public String getName() { return template.get("description.name"); }

  /**
   * @see solace.game.Player
   */
  public boolean hasName(String prefix) {
    return template.hasName(prefix);
  }

  /**
   * @see solace.game.Player
   */
  public String getDescription() { return template.get("description"); }

  /**
   * @see solace.game.Player
   */
  public void sendMessage(String msg) {
    events.trigger("message", new Object[] { msg });
  }

  /**
   * @see solace.game.Player
   */
  public void send(String msg) { sendMessage(msg); }

  /**
   * @see solace.game.Player
   */
  public void sendln(String msg) { sendMessage(msg); }

  /**
   * @see solace.game.Player
   */
  public void sendln(String... lines) { sendMessage(Joiner.on("\n\r").join(lines)); }

  /**
   * @see solace.game.Player
   */
  public void wrapln(String msg) { sendMessage(msg); }

  /**
   * @see solace.game.Player
   */
  public boolean isMobile() { return true; }

  /**
   * @see solace.game.Player
   */
  public solace.net.Connection getConnection() { return null; }

  /**
   * @return The power level for the mobile. The power level is an additional
   *   parameter that tweaks the overall power between mobiles of the same
   *   level.
   */
  public int getPower() { return power; }

  /**
   * Sets the power level of the mobile.
   * @param p Power level to set.
   */
  @SuppressWarnings("unused")
  public void setPower(int p) { power = p; }

  /**
   * @return The mobile's attack roll.
   * @see solace.game.Stats
   */
  public int getAttackRoll() { return Stats.getAttackRoll(this); }

  /**
   * TODO Should we include a mobile hit modifier in the stats engine?
   * @return The mobile's hit modifier.
   */
  public int getHitMod() { return 0; }

  /**
   * @return The mobile's average damage per attack.
   * @see solace.game.Stats
   */
  public int getAverageDamage() { return Stats.getAverageDamage(this); }

  /**
   * TODO Should we include a mobile damage modifier in the stats engine?
   * @return The mobile's damage modifier.
   * @see solace.game.Player
   */
  public int getDamageMod() { return 0; }

  /**
   * TODO Should we include multiple attacks for mobiles?
   * @see solace.game.Player
   */
  public int getNumberOfAttacks() { return 1; }

  /**
   * TODO We should add damage resistances to mobiles.
   * @see solace.game.Player
   */
  public int applyDamage(int damage) {
    hp -= damage;
    return damage;
  }

  /**
   * @see solace.game.Player
   */
  public void die(Player killer) {
    removeAllBuffs();
    setPlayState(PlayState.DEAD);
    if (killer != null) {
      Player[] excludes = { this, killer };
      getRoom().sendMessage(String.format(
        "%s has been {R}killed{x} by %s!", getName(), killer.getName()),
        excludes);
    } else {
      getRoom().sendMessage(String.format("%s has died.", getName()), this);
    }
    MobileManager.getInstance().remove(this);
  }

  /**
   * Places the mobile into the game world.
   */
  void place(Room room) {
    if (isPlaced) { return; }
    isPlaced = true;

    String spawn = template.get("description.spawn");
    if (spawn == null) {
      spawn = template.get("description.name") + " enters.";
    }
    room.sendMessage(spawn);
    setRoom(room);

    // Set the level of the mob
    level = 1;
    String levelString = template.get("level");
    if (levelString != null) {
      try {
        level = Integer.parseInt(levelString);
      }
      catch (NumberFormatException nfe) {
        Log.error(String.format(
          "Invalid level for mobile %s: %s", template.getId(), levelString
        ));
      }
    }

    power = 10;
    String powerLevel = template.get("power");
    try {
      if (powerLevel != null) {
        power = Integer.parseInt(powerLevel);
      }
    }
    catch (NumberFormatException nfe) {
      Log.error(String.format(
        "Invalid power for mobile %s: %s", template.getId(), levelString
      ));
    }

    hp = getMaxHp();

    room.getMobiles().add(this);
    room.addPlayer(this);
  }

  /**
   * Removes the mobile from the game world.
   */
  void pluck() {
    if (!isPlaced) { return; }
    Room room = getRoom();
    room.removePlayer(this);
    room.getMobiles().remove(this);
    setRoom(null);
    isPlaced = false;
  }

  /**
   * Adds an event listener to the mobile.
   * @param event Name of the event.
   * @param listener Listener for the event.
   */
  @SuppressWarnings("unused")
  public void addEventListener(String event, EventListener listener) {
    events.addListener(event, listener);
  }

  /**
   * Removes an event listener from the mobile.
   * @param event Name of the event.
   * @param listener Listener for the event.
   */
  @SuppressWarnings("unused")
  public void removeEventListener(String event, EventListener listener) {
    events.removeListener(event, listener);
  }

  /**
   * Determines if a mobile is completely protected from combat.
   * @return `true` if the mobile is protected, `false` otherwise.
   */
  public boolean isProtected() {
    return template.get("protected").equals("true") ||
      template.get("protected").equals("yes");
  }

  /**
   * Determines if this mobile is the owner of a shop with the given id.
   * @param id Id of the shop.
   * @return True if the mobile owns the shop, false otherwise.
   */
  boolean ownsShop(String id) {
    return id.equals(template.get("shop-owner"));
  }
}
