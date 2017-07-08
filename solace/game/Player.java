package solace.game;
import java.util.Collection;
import solace.util.Clock;
import solace.net.Connection;

/**
 * Behavior set for objects representing people or monsters that can inhabit
 * the game world.
 * @author Ryan Sandor Richards
 */
public interface Player {
  /**
   * Determines if the Player is a mobile.
   * @return `true` if the Player is a mobile, `false` otherwise.
   */
  public boolean isMobile();

  /**
   * @return The character for the player, or `null` if the player is a mobile.
   */
  public solace.game.Character getCharacter();

  /**
   * @return The account for the player.
   */
  public Account getAccount();

  /**
   * @return The game connection associated with the player.
   */
  public Connection getConnection();

  /**
   * @return The state of the player.
   */
  public PlayState getPlayState();

  /**
   * Sets the state for the player.
   * @param s Play state to set.
   */
  public void setPlayState(PlayState s);

  /**
   * @return `true` if the player is sleeping, `false` otherwise.
   */
  public boolean isSleeping();

  /**
   * Sets the character to be in the sleeping play state.
   */
  public void setSleeping();

  /**
   * @return `true` if the player is resting, `false` otherwise.
   */
  public boolean isResting();

  /**
   * Sets the character to be in the resting play state.
   */
  public void setResting();

  /**
   * @return `true` if the player is sitting, `false` otherwise.
   */
  public boolean isSitting();

  /**
   * Sets the character to be in the sitting play state.
   */
  public void setSitting();

  /**
   * @return `true` if the player is resting or sitting, `false` otherwise.
   */
  public boolean isRestingOrSitting();

  /**
   * @return `true` if the player is standing, `false` otherwise.
   */
  public boolean isStanding();

  /**
   * Sets the character to be in the standing play state.
   */
  public void setStanding();

  /**
   * @return `true` if the player is fighting, `false` otherwise.
   */
  public boolean isFighting();

  /**
   * Sets the character to be in the fighting play state.
   */
  public void setFighting();

  /**
   * @return `true` if the character is standing or fighting, `false` otherwise.
   */
  public boolean isStandingOrFighting();

  /**
   * Sends the Player a message coming from the room they inhabit. Examples
   * include player communication, another Player entering the room, etc.
   * @param s Message to sent the Player.
   */
  public void sendMessage(String s);

  /**
   * Sends a message to the player.
   * @param msg Message to send.
   */
  public void send(String msg);

  /**
   * Sends a message to a player with an appended new line.
   * @param msg Message to send.
   */
  public void sendln(String msg);

  /**
   * Sends a message to a player, with a prepended and appended new line.
   * @param msg Message to send.
   */
  public void wrapln(String msg);

  /**
   * @return A name by which the object is referenced.
   */
  public String getName();

  /**
   * Determines whether or not this player has a name with the given prefix.
   * @param  namePrefix Prefix by which to test.
   * @return `true` if the player has a name with the given prefix, `false`
   *   otherwise.
   */
  public boolean hasName(String namePrefix);

  /**
   * @return A string describing the object.
   */
  public String getDescription();

  /**
   * @return The room the Player currently occupies.
   */
  public Room getRoom();

  /**
   * Sets the room for the Player.
   * @param r Room to set.
   */
  public void setRoom(Room r);

  /**
   * @return The level of the player.
   */
  public int getLevel();

  /**
   * Sets the level of the player.
   * @param level Level to set.
   */
  public void setLevel(int level);

  /**
   * Sets the major statistic for the character. Major statistics grow the
   * the fastest of all stats as a character progresses in level.
   * @param name Name of the major stat.
   */
  public void setMajorStat(String name);

  /**
   * Sets the minor stat for the character. Minor stats grow at a medium pace
   * as a character levels.
   * @param name [description]
   */
  public void setMinorStat(String name);

  /**
   * @return The name of the character's major stat.
   */
  public String getMajorStat();

  /**
   * @return The name of the character's minor stat.
   */
  public String getMinorStat();

  /**
   * @return The character's strength ability score.
   */
  public int getStrength();

  /**
   * @return The character's vitality ability score.
   */
  public int getVitality();

  /**
   * @return The character's magic ability score.
   */
  public int getMagic();

  /**
   * @return The character's speed ability score.
   */
  public int getSpeed();

  /**
   * @return The current hit points of the player.
   */
  public int getHp();

  /**
   * Sets the hit points for the player.
   * @param hp Hit points to set.
   */
  public void setHp(int hp);

  /**
   * @return The maximum hit points of the player.
   */
  public int getMaxHp();

  /**
   * @return The current magic points of the player.
   */
  public int getMp();

  /**
   * Sets the magic points for the player.
   * @param mp Magic points to set.
   */
  public void setMp(int mp);

  /**
   * @return The maximum magic points of the player.
   */
  public int getMaxMp();

  /**
   * @return The current stamina points of the player.
   */
  public int getSp();

  /**
   * Sets the stamina points for the player.
   * @param sp Stamina points to set.
   */
  public void setSp(int sp);

  /**
   * @return The maximum stamina points of the player.
   */
  public int getMaxSp();

  /**
   * Gets the saving throw with the given name.
   * @param name Name of the saving throw.
   * @return The saving throw.
   * @see solace.game.Stats
   */
  public int getSavingThrow(String name);

  /**
   * Gets the magic roll for the playe for the given save.
   * @param name Name of the saving throw for which to make the roll.
   * @return The magic roll.
   */
  public int getMagicRoll(String name);

  /**
   * @return The character's will saving throw.
   */
  public int getWillSave();

  /**
   * @return The character's reflex saving throw.
   */
  public int getReflexSave();

  /**
   * @return The character's resolve saving throw.
   */
  public int getResolveSave();

  /**
   * @return The character's vigor saving throw.
   */
  public int getVigorSave();

  /**
   * @return The character's prudence saving throw.
   */
  public int getPrudenceSave();

  /**
   * @return The character's guile saving throw.
   */
  public int getGuileSave();

  /**
   * @return The player's attack roll.
   */
  public int getAttackRoll();

  /**
   * @return The player's attack roll modifier.
   */
  public int getHitMod();

  /**
   * @return The player's damage modifier.
   */
  public int getDamageMod();

  /**
   * Determines the average damage dealt by the player.
   * @return The average damage for a player.
   */
  public int getAverageDamage();

  /**
   * @return The number of attacks for the player.
   */
  public int getNumberOfAttacks();

  /**
   * @return The player's armor class.
   */
  public int getAC();

  /**
   * Applies a given amount of damage to the player.
   * @param d Damage to apply.
   * @return The actual damage dealt after applying resistances, etc.
   */
  public int applyDamage(int d);

  /**
   * @return `true` if the player is dead, `false` otherwise.
   */
  public boolean isDead();

  /**
   * Manages player death in the game world.
   */
  public void die(Player killer);

  /**
   * @return True if the player is on the global cooldown, false otherwise.
   */
  public boolean isOnGCD();

  /**
   * Sets the player to be on the global cooldown.
   */
  public void setOnGCD();

  /**
   * Indicates to the player a non-gcd cooldown action has been executed and
   * will cool down after the given number of seconds.
   * @param name Name of the cooldown.
   * @param duration Duration in seconds to cool down.
   */
  public void cooldownAt(String name, int duration);

  /**
   * Determines the amount of time remaining for a cooldown of the given name.
   * @param name Name of the cooldown.
   * @return The number of seconds remaining on the cool down.
   */
  public int getCooldownDuration(String name);

  /**
   * Sets the combo action for a player. This is used to determine if other
   * skill actions should execute as a combos.
   * @param action Name of the combo action to set.
   */
  public void setComboAction(String action);

  /**
   * @return The combo action (if any) for the player.
   */
  public String getComboAction();

  /**
   * Determines if the player has a passive of the given name.
   * @param name Name of the passive.
   * @return True if they have the passive, false otherwise.
   */
  public boolean hasPassive(String name);

  /**
   * Gets the level for the named passive.
   * @param name Name of the passive ability.
   * @return The level of the passive ability, or -1 if the character does not
   *   possess the given passive.
   */
  public int getPassiveLevel(String name);

  /**
   * @return A list of passive abilities for the player.
   */
  public Collection<String> getPassives();

  /**
   * Determines if a player has a given cooldown action.
   * @param name Name of the cooldown action.
   * @return True if the player possesses the given cooldown, false otherwise.
   */
  public boolean hasCooldown(String name);

  /**
   * Gets the level for a cooldown action of the given name.
   * @param name Name of the cooldown action.
   * @return The level of the cooldown, or -1 if the player does not possess
   *   the named cooldown action.
   */
  public int getCooldownLevel(String name);

  /**
   * @return A list of cooldowns for the player.
   */
  public Collection<String> getCooldowns();

  // ---

  /**
   * Determines if the player is currently affected by a buff of the given name.
   * @param name Name of the buff.
   * @return True if the player is affected, false otherwise.
   */
  public boolean hasBuff(String name);

  /**
   * Gets the buff for the player of the given name.
   * @param name Name of the buff to get.
   * @return The buff of the given name, or null if no such buff is affecting
   *   the player.
   */
  public Buff getBuff(String name);

  /**
   * Applies the given buff to the player. This will reapply the buff if it is
   * already present.
   * @param b The buff to apply.
   */
  public void applyBuff(Buff b);

  /**
   * Applies a new buff of the given name to the player.
   * @param name Name of the buff to apply.
   */
  public void applyBuff(String name);

  /**
   * Remove a buff of the given name from the player. Has no effect if the
   * player is not affected by a buff of the given name.
   * @param name Name of the buff to remove.
   */
  public void removeBuff(String name);

  /**
   * @return A collection of all buffs currently affecting the player.
   */
  public Collection<Buff> getBuffs();

  /**
   * Refreshes buffs for the player and removes those that have expired.
   */
  public void removeExpiredBuffs();

  /**
   * Removes all buffs from the player.
   */
  public void removeAllBuffs();

  /**
   * @return `true` if the player is flagged as immortal, `false` otherwise.
   */
  public boolean isImmortal();

  /**
   * Toggles the player between being flagged as immortal and not.
   * @param setter Player who is initiated the change of immortal status.
   */
  public void toggleImmortal(Player setter);

  /**
   * Sets whether or not the player is flagged as immortal.
   * @param i `true` to flag as immortal, `false` otherwise.
   */
  public void setImmortal(boolean i);

  /**
   * Sets whether or not the player is flagged as immortal.
   * @param i `true` to flag as immortal, `false` otherwise.
   * @param setter Player who is initiated the change of immortal status.
   */
  public void setImmortal(boolean i, Player setter);

  /**
   * Determines whether or not this player is visible to given viewer.
   * @param viewer Viewer of this player.
   * @return `true` if the player is visible, `false` otherwise.
   */
  public boolean isVisibleTo(Player viewer);

  /**
   * Resets the visibility state of the player by removing appropriate buffs
   * given the name of an event that has occurred involving the player. Whether
   * or not visibility altering buffs are removed depends on the narture of the
   * buff and what action has been taken.
   * @param event Name of the action that has occured involving the player.
   */
  public void resetVisibilityOnAction(String event);

  /**
   * @return `true` if the player is casting a spell, `false` otherwise.
   */
  public boolean isCasting();

  /**
   * Sets whether or not the player is casting a spell.
   * @param e The event associated with the casting of the spell.
   */
  public void beginCasting(Clock.Event e);

  /**
   * Interrupt the player if they are currently casting a spell.
   */
  public void interruptCasting();

  /**
   * Cleanup after spell is successfuly cast.
   */
  public void finishCasting();

  /**
   * @return The battle the player is currently engaged in, if applicable.
   *   `null` otheriws.e
   */
  public Battle getBattle();

  /**
   * Applies a "Damage over time" (DoT) buff to the player.
   * TODO We may remove this once we have fully scriptable buffs.
   * @param String name Name of the buff.
   * @param int    avg  Average damage.
   * @param int    d    Duration for the buff.
   * @param int    f    Frequency in ticks when to apply damage (round = 2).
   * @param String msg  Damage message format.
   */
  public void applyDot(String name, int avg, int d, int f, String msg);
}
