package solace.game;

import solace.util.Buffs;
import java.util.Date;

/**
 * Represents a temporary passive ability that affects the player in some way.
 * Examples include:
 *
 * - 10% Bonus Speed for 30 seconds
 * - Double potency of cooldowns for 10 seconds
 * - Reduce AC by 10% for 15 seconds
 * - Increase bartering effectiveness for 2 minutes
 * - Vanish from sight indefinitely until moving or attacking
 *
 * As is noted above a buff may affect a player in a positive or negative way.
 * When a buff affects a player in a negative way it is called a "debuff".
 *
 * @author Ryan Sandor Richards
 */
public class Buff {
  public static int TIME_REMAINING_EXPIRED = 0;
  public static int TIME_REMAINING_INDEFINATE = -1;

  String name;
  int level;
  Date expiry;
  boolean indefinite = false;

  /**
   * Creates a new buff with the given name and duration.
   * @param n Name for the buff.
   * @param s Duration in seconds the buff will last.
   */
  public Buff(String n, int s) {
    name = n;
    if (s < 0) {
      indefinite = true;
    }
    expiry = new Date(new Date().getTime() + 1000 * s);
  }

  /**
   * @return The name of the buff.
   */
  public String getName() { return name; }

  /**
   * Sets the name for the buff.
   * @param name Name to set.
   */
  public void setName(String n) { name = n; }

  /**
   * @return The level of the buff.
   */
  public int getLevel() { return level; }

  /**
   * Sets the level of the buff.
   * @param l Level to set.
   */
  public void setLevel(int l) { level = l; }

  /**
   * @return The description of the buff.
   */
  public String getDescription() { return Buffs.getDescription(name); }

  /**
   * @return True if this is a debuff, false otherwise.
   */
  public boolean isDebuff() { return Buffs.isDebuff(name); }

  /**
   * @return The begin message for targets of the buff.
   */
  public String getTargetBeginMessage() {
    return Buffs.getTargetBeginMessage(name);
  }

  /**
   * @return The end message for targets of the buff.
   */
  public String getTargetEndMessage() {
    return Buffs.getTargetEndMessage(name);
  }

  /**
   * @return The begin message for observers of the buff.
   */
  public String getObserverBeginMessage() {
    return Buffs.getObserverBeginMessage(name);
  }

  /**
   * @return The end message for observers of the buff.
   */
  public String getObserverEndMessage() {
    return Buffs.getObserverEndMessage(name);
  }

  /**
   * Determines if the buff has expired.
   * @return True if the buff has expired, false otherwise.
   */
  public boolean hasExpired() {
    if (indefinite) {
      return false;
    }
    return new Date().after(expiry);
  }

  /**
   * Sets the buff as immediately expired. Useful for spells that strip buffs
   * and handling the expiry of indefinite length buffs (e.g. vanish, hide,
   * etc.).
   */
  public void setExpired() {
    indefinite = false;
    expiry = new Date(new Date().getTime() - 1000 * 86400);
  }

  /**
   * @return The number of seconds remaining until the buff expires or -1 if the
   *   buff has already expired.
   */
  public int getTimeRemaining() {
    if (indefinite) return TIME_REMAINING_INDEFINATE;
    if (hasExpired()) return TIME_REMAINING_EXPIRED;
    return Math.max(
      0, (int)((expiry.getTime() - new Date().getTime()) / 1000));
  }

  /**
   * Allows subclasses to schedule actions to occur using a game clock interval.
   * Useful for buffs such as "regenerating" and debuffs such as "poisoned". By
   * default this method has no effect.
   */
  public void scheduleTickAction() {
  }

  /**
   * Interface for cancelling scheudled tick actions for buffs. This method is
   * intended for use by subclasses and by default it has no effect.
   */
  public void cancelTickAction() {
  }
}
