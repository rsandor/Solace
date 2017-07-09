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
  boolean isMobile();

  /**
   * @return The character for the player, or `null` if the player is a mobile.
   */
  solace.game.Character getCharacter();

  /**
   * @return The account for the player.
   */
  Account getAccount();

  /**
   * @return The game connection associated with the player.
   */
  Connection getConnection();

  /**
   * @return The state of the player.
   */
  PlayState getPlayState();

  /**
   * Sets the state for the player.
   * @param s Play state to set.
   */
  void setPlayState(PlayState s);

  /**
   * @return `true` if the player is sleeping, `false` otherwise.
   */
  boolean isSleeping();

  /**
   * Sets the character to be in the sleeping play state.
   */
  void setSleeping();

  /**
   * @return `true` if the player is resting, `false` otherwise.
   */
  boolean isResting();

  /**
   * Sets the character to be in the resting play state.
   */
  void setResting();

  /**
   * @return `true` if the player is sitting, `false` otherwise.
   */
  boolean isSitting();

  /**
   * Sets the character to be in the sitting play state.
   */
  void setSitting();

  /**
   * @return `true` if the player is resting or sitting, `false` otherwise.
   */
  boolean isRestingOrSitting();

  /**
   * @return `true` if the player is standing, `false` otherwise.
   */
  boolean isStanding();

  /**
   * Sets the character to be in the standing play state.
   */
  void setStanding();

  /**
   * @return `true` if the player is fighting, `false` otherwise.
   */
  boolean isFighting();

  /**
   * Sets the character to be in the fighting play state.
   */
  void setFighting();

  /**
   * @return `true` if the character is standing or fighting, `false` otherwise.
   */
  boolean isStandingOrFighting();

  /**
   * Sends the Player a message coming from the room they inhabit. Examples
   * include player communication, another Player entering the room, etc.
   * @param s Message to sent the Player.
   */
  void sendMessage(String s);

  /**
   * Sends a message to the player.
   * @param msg Message to send.
   */
  void send(String msg);

  /**
   * Sends a message to a player with an appended new line.
   * @param msg Message to send.
   */
  void sendln(String msg);

  /**
   * Sends each line given as a parameter to the player.
   * @param lines Lines to send to the player.
   */
  void sendln(String... lines);

  /**
   * Sends a message to a player, with a prepended and appended new line.
   * @param msg Message to send.
   */
  void wrapln(String msg);

  /**
   * @return A name by which the object is referenced.
   */
  String getName();

  /**
   * Determines whether or not this player has a name with the given prefix.
   * @param  namePrefix Prefix by which to test.
   * @return `true` if the player has a name with the given prefix, `false`
   *   otherwise.
   */
  boolean hasName(String namePrefix);

  /**
   * @return A string describing the object.
   */
  String getDescription();

  /**
   * @return The room the Player currently occupies.
   */
  Room getRoom();

  /**
   * Sets the room for the Player.
   * @param r Room to set.
   */
  void setRoom(Room r);

  /**
   * @return The level of the player.
   */
  int getLevel();

  /**
   * Sets the level of the player.
   * @param level Level to set.
   */
  void setLevel(int level);

  /**
   * Sets the major statistic for the character. Major statistics grow the
   * the fastest of all stats as a character progresses in level.
   * @param name Name of the major stat.
   */
  void setMajorStat(String name);

  /**
   * Sets the minor stat for the character. Minor stats grow at a medium pace
   * as a character levels.
   * @param name [description]
   */
  void setMinorStat(String name);

  /**
   * @return The name of the character's major stat.
   */
  String getMajorStat();

  /**
   * @return The name of the character's minor stat.
   */
  String getMinorStat();

  /**
   * @return The character's strength ability score.
   */
  int getStrength();

  /**
   * @return The character's vitality ability score.
   */
  int getVitality();

  /**
   * @return The character's magic ability score.
   */
  int getMagic();

  /**
   * @return The character's speed ability score.
   */
  int getSpeed();

  /**
   * @return The current hit points of the player.
   */
  int getHp();

  /**
   * Sets the hit points for the player.
   * @param hp Hit points to set.
   */
  void setHp(int hp);

  /**
   * @return The maximum hit points of the player.
   */
  int getMaxHp();

  /**
   * @return The current magic points of the player.
   */
  int getMp();

  /**
   * Sets the magic points for the player.
   * @param mp Magic points to set.
   */
  void setMp(int mp);

  /**
   * @return The maximum magic points of the player.
   */
  int getMaxMp();

  /**
   * @return The current stamina points of the player.
   */
  int getSp();

  /**
   * Sets the stamina points for the player.
   * @param sp Stamina points to set.
   */
  void setSp(int sp);

  /**
   * @return The maximum stamina points of the player.
   */
  int getMaxSp();

  /**
   * Gets the saving throw with the given name.
   * @param name Name of the saving throw.
   * @return The saving throw.
   * @see solace.game.Stats
   */
  int getSavingThrow(String name);

  /**
   * Gets the magic roll for the playe for the given save.
   * @param name Name of the saving throw for which to make the roll.
   * @return The magic roll.
   */
  int getMagicRoll(String name);

  /**
   * @return The character's will saving throw.
   */
  int getWillSave();

  /**
   * @return The character's reflex saving throw.
   */
  int getReflexSave();

  /**
   * @return The character's resolve saving throw.
   */
  int getResolveSave();

  /**
   * @return The character's vigor saving throw.
   */
  int getVigorSave();

  /**
   * @return The character's prudence saving throw.
   */
  int getPrudenceSave();

  /**
   * @return The character's guile saving throw.
   */
  int getGuileSave();

  /**
   * @return The player's attack roll.
   */
  int getAttackRoll();

  /**
   * @return The player's attack roll modifier.
   */
  int getHitMod();

  /**
   * @return The player's damage modifier.
   */
  int getDamageMod();

  /**
   * Determines the average damage dealt by the player.
   * @return The average damage for a player.
   */
  int getAverageDamage();

  /**
   * @return The number of attacks for the player.
   */
  int getNumberOfAttacks();

  /**
   * @return The player's armor class.
   */
  int getAC();

  /**
   * Applies a given amount of damage to the player.
   * @param d Damage to apply.
   * @return The actual damage dealt after applying resistances, etc.
   */
  int applyDamage(int d);

  /**
   * @return `true` if the player is dead, `false` otherwise.
   */
  boolean isDead();

  /**
   * Manages player death in the game world.
   */
  void die(Player killer);

  /**
   * @return True if the player is on the global cooldown, false otherwise.
   */
  boolean isOnGCD();

  /**
   * Sets the player to be on the global cooldown.
   */
  void setOnGCD();

  /**
   * Indicates to the player a non-gcd cooldown action has been executed and
   * will cool down after the given number of seconds.
   * @param name Name of the cooldown.
   * @param duration Duration in seconds to cool down.
   */
  void setOnCooldown(String name, int duration);

  /**
   * Determines if the action with the given name is on cooldown.
   * @param name Name of the cooldown action.
   * @return True if it is on cooldown for the player, false otherwise.
   */
  boolean isOnCooldown(String name);

  /**
   * Determines the amount of time remaining for a cooldown of the given name.
   * @param name Name of the cooldown.
   * @return The number of seconds remaining on the cool down.
   */
  int getCooldownDuration(String name);

  /**
   * Sets the combo action for a player. This is used to determine if other
   * skill actions should execute as a combos.
   * @param action Name of the combo action to set.
   */
  void setComboAction(String action);

  /**
   * @return The combo action (if any) for the player.
   */
  String getComboAction();

  /**
   * Determines if the player has a passive of the given name.
   * @param name Name of the passive.
   * @return True if they have the passive, false otherwise.
   */
  boolean hasPassive(String name);

  /**
   * Gets the level for the named passive.
   * @param name Name of the passive ability.
   * @return The level of the passive ability, or -1 if the character does not
   *   possess the given passive.
   */
  int getPassiveLevel(String name);

  /**
   * @return A list of passive abilities for the player.
   */
  Collection<String> getPassives();

  /**
   * Determines if a player has a given cooldown action.
   * @param name Name of the cooldown action.
   * @return True if the player possesses the given cooldown, false otherwise.
   */
  boolean hasCooldown(String name);

  /**
   * Gets the level for a cooldown action of the given name.
   * @param name Name of the cooldown action.
   * @return The level of the cooldown, or -1 if the player does not possess
   *   the named cooldown action.
   */
  int getCooldownLevel(String name);

  /**
   * @return A list of cooldowns for the player.
   */
  Collection<String> getCooldowns();

  /**
   * Determines if the player is currently affected by a buff of the given name.
   * @param name Name of the buff.
   * @return True if the player is affected, false otherwise.
   */
  boolean hasBuff(String name);

  /**
   * Gets the buff for the player of the given name.
   * @param name Name of the buff to get.
   * @return The buff of the given name, or null if no such buff is affecting
   *   the player.
   */
  Buff getBuff(String name);

  /**
   * Applies the given buff to the player. This will reapply the buff if it is
   * already present.
   * @param b The buff to apply.
   */
  void applyBuff(Buff b);

  /**
   * Applies a new buff of the given name to the player.
   * @param name Name of the buff to apply.
   */
  void applyBuff(String name);

  /**
   * Remove a buff of the given name from the player. Has no effect if the
   * player is not affected by a buff of the given name.
   * @param name Name of the buff to remove.
   */
  void removeBuff(String name);

  /**
   * @return A collection of all buffs currently affecting the player.
   */
  Collection<Buff> getBuffs();

  /**
   * Refreshes buffs for the player and removes those that have expired.
   */
  void removeExpiredBuffs();

  /**
   * Removes all buffs from the player.
   */
  void removeAllBuffs();

  /**
   * @return `true` if the player is flagged as immortal, `false` otherwise.
   */
  boolean isImmortal();

  /**
   * Toggles the player between being flagged as immortal and not.
   * @param setter Player who is initiated the change of immortal status.
   */
  void toggleImmortal(Player setter);

  /**
   * Sets whether or not the player is flagged as immortal.
   * @param i `true` to flag as immortal, `false` otherwise.
   */
  void setImmortal(boolean i);

  /**
   * Sets whether or not the player is flagged as immortal.
   * @param i `true` to flag as immortal, `false` otherwise.
   * @param setter Player who is initiated the change of immortal status.
   */
  void setImmortal(boolean i, Player setter);

  /**
   * Determines whether or not this player is visible to given viewer.
   * @param viewer Viewer of this player.
   * @return `true` if the player is visible, `false` otherwise.
   */
  boolean isVisibleTo(Player viewer);

  /**
   * Resets the visibility state of the player by removing appropriate buffs
   * given the name of an event that has occurred involving the player. Whether
   * or not visibility altering buffs are removed depends on the narture of the
   * buff and what action has been taken.
   * @param event Name of the action that has occured involving the player.
   */
  void resetVisibilityOnAction(String event);

  /**
   * @return `true` if the player is casting a spell, `false` otherwise.
   */
  boolean isCasting();

  /**
   * Sets whether or not the player is casting a spell.
   * @param e The event associated with the casting of the spell.
   */
  void beginCasting(Clock.Event e);

  /**
   * Interrupt the player if they are currently casting a spell.
   */
  void interruptCasting();

  /**
   * Cleanup after spell is successfuly cast.
   */
  void finishCasting();

  /**
   * @return The battle the player is currently engaged in, if applicable.
   *   `null` otheriws.e
   */
  Battle getBattle();

  /**
   * Applies a "Damage over time" (DoT) buff to the player.
   * TODO We may remove this once we have fully scriptable buffs.
   * @param String name Name of the buff.
   * @param int    avg  Average damage.
   * @param int    d    Duration for the buff.
   * @param int    f    Frequency in ticks when to apply damage (round = 2).
   * @param String msg  Damage message format.
   */
  void applyDot(String name, int avg, int d, int f, String msg);
}
