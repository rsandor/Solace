package solace.game;

/**
 * Behavior set for objects representing people or monsters that can inhabit
 * the game world.
 * @author Ryan Sandor Richards
 */
public interface Player {
  /**
   * @return A name by which the object is referenced.
   */
  public String getName();

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
   * Determines if the Player is a mobile.
   * @return `true` if the Player is a mobile, `false` otherwise.
   */
  public boolean isMobile();

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
   * @return The level of the player.
   */
  public int getLevel();

  /**
   * @return The current HP of the player.
   */
  public int getHp();

  /**
   * @return The maximum HP of the player.
   */
  public int getMaxHp();

  /**
   * Determines whether or not this player has a name with the given prefix.
   * @param  namePrefix Prefix by which to test.
   * @return `true` if the player has a name with the given prefix.
   *  `false` otherwise.
   */
  public boolean hasName(String namePrefix);

  /**
   * Determines if the player has a passive of the given name.
   * @param  name Name of the passive.
   * @return      True if they have the passive, false otherwise.
   */
  public boolean hasPassive(String name);

  /**
   * Determines if the character parries an attack.
   * @return True if the attack is parried, false otherwise.
   */
  public boolean parry();

  /**
   * Gets the level of the highest skill which grants the given cooldown.
   * @param  name Name of the cooldown.
   * @return      The skill level, or -1 if the player does not have that skill.
   */
  public int getCooldownSkillLevel(String name);

  /**
   * @return True if the player is on the global cooldown, false otherwise.
   */
  public boolean isOnGCD();

  /**
   * Sets the player to be on the global cooldown.
   */
  public void setOnGCD();

  /**
   * Sets the level of the player.
   * @param level Level to set.
   */
  public void setLevel(int level);

  /**
   * Sets the hit points for the player.
   * @param hp Hit points to set.
   */
  public void setHp(int hp);
}
