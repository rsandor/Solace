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
 *
 * As is noted above a buff may affect a player in a positive or negative way.
 * When a buff affects a player in a negative way it is called a "debuff".
 *
 * @author Ryan Sandor Richards
 */
public class Buff {
  String name;
  int level;
  Date expiry;

  /**
   * Creates a new buff with the given name and duration.
   * @param n Name for the buff.
   * @param s Duration in seconds the buff will last.
   */
  public Buff(String n, int s) {
    name = n;
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
   * Determines if the buff has expired.
   * @return True if the buff has expired, false otherwise.
   */
  public boolean hasExpired() {
    return new Date().after(expiry);
  }

  /**
   * @return The number of seconds remaining until the buff expires or -1 if the
   *   buff has already expired.
   */
  public int getTimeRemaining() {
    if (hasExpired()) return -1;
    return (int)((expiry.getTime() - new Date().getTime()) / 1000);
  }
}
