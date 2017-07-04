package solace.game;

import solace.util.Clock;
import solace.util.Log;
import solace.util.Buffs;
import java.util.*;

/**
 * Implements common functionality of the player interface shared by both
 * characters and mobiles.
 * @author Ryan Sandor Richards
 */
public abstract class AbstractPlayer implements Player {
  /**
   * Timer for tracking cooldown durations.
   */
  protected class CooldownTimer {
    Date usedAt;
    long duration;

    /**
     * Creates a new cooldown timer for the given duration.
     * @param d Duration in seconds when the skill will be available.
     */
    public CooldownTimer(int d) {
      usedAt = new Date();
      duration = (long)d;
    }

    /**
     * @return The time remaining, in seconds, until the cool down is complete.
     */
    public int getTimeRemaining() {
      long elapsed = new Date().getTime() - usedAt.getTime();
      elapsed /= 1000;
      return (int)Math.max(0, duration - elapsed);
    }
  }

  // Instance variables
  boolean immortal = false;
  PlayState state = PlayState.STANDING;
  Room room = null;
  int level;
  int hp;
  int mp;
  int sp;
  String majorStat = "none";
  String minorStat = "none";

  boolean onGCDCooldown = false;
  String comboAction;
  boolean casting = false;
  Clock.Event castingEvent;

  Hashtable<String, CooldownTimer> cooldownTimers =
    new Hashtable<String, CooldownTimer>();
  Hashtable<String, Integer> passives = new Hashtable<String, Integer>();
  Hashtable<String, Integer> cooldowns = new Hashtable<String, Integer>();
  Hashtable<String, Buff> buffs = new Hashtable<String, Buff>();

  // Abstract Player Methods
  public abstract void die(Player killer);
  public abstract boolean isMobile();
  public abstract void sendMessage(String s);
  public abstract void send(String msg);
  public abstract void sendln(String msg);
  public abstract void wrapln(String msg);
  public abstract String getName();
  public abstract boolean hasName(String namePrefix);
  public abstract String getDescription();
  public abstract int getAttackRoll();
  public abstract int getHitMod();
  public abstract int getDamageMod();
  public abstract int getAverageDamage();
  public abstract int getNumberOfAttacks();

  /**
   * Sets the passives and cooldowns for this character. This method should be
   * called after loading a character, on level, skill level, or when acquiring
   * a new skill.
   */
  public void setPassivesAndCooldowns() {
    passives.clear();
    cooldowns.clear();
  }

  /**
   * @see solace.game.Player
   */
  public int getSavingThrow(String name) {
    try {
      return Stats.getSavingThrow(this, name);
    } catch (InvalidSavingThrowException e) {
      Log.error(String.format(
        "Invalid saving throw name encountered: %s", name));
    }
    return 0;
  }

  /**
   * @see solace.game.Player
   */
  public int getMagicRoll(String saveName) {
    try {
      return Stats.getMagicRoll(this, saveName);
    } catch (InvalidSavingThrowException e) {
      Log.error(String.format(
        "Magic roll attempted with invalid saving throw: %s",
        saveName));
      return 0;
    }
  }

  /**
   * Determines the ability score of the given name.
   * @param name Name of the ability score.
   * @return The ability score for this player.
   * @see solace.game.Stats.getAbility(Player, String)
   */
  protected int getAbility(String name) {
    return Stats.getAbility(this, name);
  }

  /**
   * Gets the maximum value for the given resource.
   * @param name Name of the resource.
   * @return The maximum value of the resource for this player, or -1 if the
   *   provided resource name is invalid.
   */
  protected int getMaxResource(String name) {
    if (name.equals("hp")) {
      return Stats.getMaxHp(this);
    } else if (name.equals("mp")) {
      return Stats.getMaxMp(this);
    } else if (name.equals("sp")) {
      return Stats.getMaxSp(this);
    }
    return -1;
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
   * @see solace.game.Player
   */
  public boolean isSleeping() {
    return state == PlayState.SLEEPING;
  }

  /**
   * @see solace.game.Player
   */
  public void setSleeping() {
    state = PlayState.SLEEPING;
  }

  /**
   * @see solace.game.Player
   */
  public boolean isResting() {
    return state == PlayState.RESTING;
  }

  /**
   * @see solace.game.Player
   */
  public void setResting() {
    state = PlayState.RESTING;
  }

  /**
   * @see solace.game.Player
   */
  public boolean isSitting() {
    return state == PlayState.SITTING;
  }

  /**
   * @see solace.game.Player
   */
  public void setSitting() {
    state = PlayState.SITTING;
  }

  /**
   * @see solace.game.Player
   */
  public boolean isRestingOrSitting() {
    return state == PlayState.RESTING || state == PlayState.SITTING;
  }

  /**
   * @see solace.game.Player
   */
  public boolean isStanding() {
    return state == PlayState.STANDING;
  }

  /**
   * @see solace.game.Player
   */
  public void setStanding() {
    state = PlayState.STANDING;
  }

  /**
   * @see solace.game.Player
   */
  public boolean isFighting() {
    return state == PlayState.FIGHTING;
  }

  /**
   * @see solace.game.Player
   */
  public void setFighting() {
    state = PlayState.FIGHTING;
  }

  /**
   * @see solace.game.Player
   */
  public boolean isStandingOrFighting() {
    return state == PlayState.FIGHTING || state == PlayState.STANDING;
  }

  /**
   * @see solace.game.Player
   */
  public Room getRoom() {
    return room;
  }

  /**
   * @see solace.game.Player
   */
  public void setRoom(Room r) {
    room = r;
  }

  /**
   * @see solace.game.Player
   */
  public int getLevel() { return level; }

  /**
   * @see solace.game.Player
   */
  public void setLevel(int l) { level = l; }

  /**
   * @see solace.game.Player
   */
  public void setMajorStat(String name) { majorStat = name; }

  /**
   * @see solace.game.Player
   */
  public void setMinorStat(String name) { minorStat = name; }

  /**
   * @see solace.game.Player
   */
  public String getMajorStat() { return majorStat; }

  /**
   * @see solace.game.Player
   */
  public String getMinorStat() { return minorStat; }

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
   * @see solace.game.Player
   */
  public int getMaxHp() { return getMaxResource("hp"); }

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
   * @see solace.game.Player
   */
  public int getMaxMp() { return getMaxResource("mp"); }

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
   * @see solace.game.Player
   */
  public int getMaxSp() { return getMaxResource("sp"); }

  /**
   * @see solace.game.Player
   */
  public int getStrength() { return getAbility("strength"); }

  /**
   * @see solace.game.Player
   */
  public int getVitality() {
    int vit = getAbility("vitality");
    return hasPassive("stout-hearted") ? (int)(1.1 * vit) : vit;
  }

  /**
   * @see solace.game.Player
   */
  public int getMagic() { return getAbility("magic"); }

  /**
   * @see solace.game.Player
   */
  public int getSpeed() {
    int spe = getAbility("speed");
    return hasPassive("light-footed") ? (int)(1.1 * spe) : spe;
  }

  /**
   * @see solace.game.Player
   */
  public int getWillSave() { return getSavingThrow("will"); }

  /**
   * @see solace.game.Player
   */
  public int getReflexSave() { return getSavingThrow("reflex"); }

  /**
   * @see solace.game.Player
   */
  public int getResolveSave() { return getSavingThrow("resolve"); }

  /**
   * @see solace.game.Player
   */
  public int getVigorSave() { return getSavingThrow("vigor"); }

  /**
   * @see solace.game.Player
   */
  public int getPrudenceSave() { return getSavingThrow("prudence"); }

  /**
   * @see solace.game.Player
   */
  public int getGuileSave() { return getSavingThrow("guile"); }

  /**
   * @return The player's armor class.
   */
  public int getAC() { return Stats.getAC(this); }

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
  public boolean isDead() {
    return getHp() <= 0;
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

  /**
   * @see solace.game.Player
   */
  public void cooldownAt(String name, int duration) {
    cooldownTimers.put(name, new CooldownTimer(duration));
  }

  /**
  * @see solace.game.Player
  */
  public int getCooldownDuration(String name) {
    if (cooldownTimers.containsKey(name)) {
      return cooldownTimers.get(name).getTimeRemaining();
    }
    return -1;
  }

  /**
   * @see solace.game.Player
   */
  public void setComboAction(String action) {
    comboAction = action;
  }

  /**
   * @see solace.game.Player
   */
  public String getComboAction() {
    return comboAction == null ? "" : comboAction;
  }

  /**
   * @see solace.game.Player
   */
  public boolean hasPassive(String name) {
    return passives.containsKey(name);
  }

  /**
   * @see solace.game.Player
   */
  public int getPassiveLevel(String name) {
    if (!hasPassive(name)) return -1;
    return passives.get(name);
  }

  /**
   * Sets a passive on the player for the given name at the given level.
   * @param name Name of the passive.
   * @param level Level of the passive.
   */
  protected void setPassive(String name, int level) {
    passives.put(name, level);
  }

  /**
   * @see solace.game.Player
   */
  public Collection<String> getPassives() {
    return Collections.unmodifiableCollection(passives.keySet());
  }

  /**
   * @see solace.game.Player
   */
  public boolean hasCooldown(String name) {
    return cooldowns.containsKey(name);
  }

  /**
   * @see solace.game.Player
   */
  public int getCooldownLevel(String name) {
    if (!hasCooldown(name)) return -1;
    return cooldowns.get(name);
  }

  /**
   * Sets a cooldown on the player for the given name at the given level.
   * @param name Name of the cooldown.
   * @param level Level of the cooldown.
   */
  protected void setCooldown(String name, int level) {
    cooldowns.put(name, level);
  }

  /**
   * @see solace.game.Player
   */
  public Collection<String> getCooldowns() {
    return Collections.unmodifiableCollection(cooldowns.keySet());
  }

  /**
   * Sends messages to player when a buff is applied.
   * @param b Buff begin applied.
   */
  protected void sendBuffBeginMessages(Buff b) {
    String targetBeginMessage = b.getTargetBeginMessage();
    if (targetBeginMessage != null && !targetBeginMessage.equals("")) {
      sendln(targetBeginMessage);
    }
    String observerBeginMessage = b.getObserverBeginMessage();
    if (observerBeginMessage != null && !observerBeginMessage.equals("")) {
      getRoom().sendMessage(String.format(
        observerBeginMessage, getName()), this);
    }
  }

  /**
   * Sends messages to player when a buff is removed.
   * @param b Buff begin removed.
   */
  protected void sendBuffEndMessages(Buff b) {
    String targetEndMessage = b.getTargetEndMessage();
    if (targetEndMessage != null && !targetEndMessage.equals("")) {
      sendln(targetEndMessage);
    }
    String observerEndMessage = b.getObserverEndMessage();
    if (observerEndMessage != null && !observerEndMessage.equals("")) {
      getRoom().sendMessage(String.format(
        observerEndMessage, getName()), this);
    }
  }

  /**
   * @see solace.game.Player
   */
  public void applyBuff(Buff b) {
    String name = b.getName();
    if (hasBuff(name)) {
      Buff oldBuff = getBuff(name);
      oldBuff.cancelTickAction();
      buffs.remove(name); // Directly remove so we send no messages...
    }
    sendBuffBeginMessages(b);
    buffs.put(b.getName(), b);
    b.scheduleTickAction();
  }

  /**
   * @see solace.game.Player
   */
  public void applyBuff(String name) {
    if (hasBuff(name)) {
      Buff oldBuff = getBuff(name);
      oldBuff.cancelTickAction();
      buffs.remove(name); // Directly remove so we send no messages...
    }
    Buff b = Buffs.create(name);
    sendBuffBeginMessages(b);
    buffs.put(name, b);
    b.scheduleTickAction();
  }

  /**
   * @see solace.game.Player
   */
  public boolean hasBuff(String name) {
    return buffs.containsKey(name);
  }

  /**
   * @see solace.game.Player
   */
  public Buff getBuff(String name) {
    return buffs.get(name);
  }

  /**
   * @see solace.game.Player
   */
  public void removeBuff(String name) {
    if (!hasBuff(name)) {
      return;
    }
    Buff b = getBuff(name);
    b.cancelTickAction();
    buffs.remove(name);
    sendBuffEndMessages(b);
  }

  /**
   * @see solace.game.Player
   */
  public Collection<Buff> getBuffs() {
    synchronized (buffs) {
      List<Buff> remove = new LinkedList<Buff>();
      for (Buff b : buffs.values()) {
        if (!b.hasExpired()) continue;
        remove.add(b);
      }
      for (Buff b : remove) {
        removeBuff(b.getName());
      }
      return buffs.values();
    }
  }

  /**
   * @see solace.game.Player
   */
  public void removeExpiredBuffs() {
    synchronized (buffs) {
      List<Buff> remove = new LinkedList<Buff>();
      for (Buff b : buffs.values()) {
        if (b.hasExpired()) {
          removeBuff(b.getName());
        }
      }
    }
  }

  /**
   * @see solace.game.Player
   */
  public boolean isImmortal() {
    return immortal;
  }

  /**
   * @see solace.game.Player
   */
  public void toggleImmortal(Player setter) {
    setImmortal(!immortal, setter);
  }

  /**
   * @see solace.game.Player
   */
  public void setImmortal(boolean i) {
    immortal = i;
  }

  /**
   * @see solace.game.Player
   */
  public void setImmortal(boolean i, Player setter) {
    immortal = i;
    if (immortal) {
      Log.warn(String.format(
        "Player '%s' has been set as immortal by player '%s'",
        getName(), setter.getName()));
      sendMessage(String.format(
        "You have been granted immortality! You are now impervious to damage!"
        ));
      setter.sendMessage(String.format("%s is now immortal.", getName()));
    } else {
      Log.warn(String.format(
        "Player '%s' has been has been set as NOT immortal by player '%s'",
        getName(), setter.getName()));
      sendMessage(String.format(
        "You are no longer immortal! Damage now applies as normal"
        ));
      setter.sendMessage(String.format("%s is no longer immortal.", getName()));
    }
  }

  /**
   * @see solace.game.Player
   */
  public boolean isVisibleTo(Player viewer) {
    // TODO Add a special buff that allows players to see "vanished" players
    //      this should not be easy to attain for player characters, but some
    //      "god" level mobs should always have the buff.
    if (hasBuff("vanished")) {
      return false;
    }
    return true;
  }

  /**
   * @see solace.game.Player
   */
  public void resetVisibilityOnAction(String event) {
    // TODO Various buffs need to be added to make this implementation more
    //      interesting and vairable depending on the situation. For the time
    //      being basically everything that can drop the "vanished" buff will
    //      do so (helps us avoid having to fix all messaging around movement
    //      dropping items, etc., for instance: "Someone moves west..." or
    //      simply omitting the message at all, etc.).
    if (hasBuff("vanished")) {
      removeBuff("vanished");
    }
  }

  /**
   * @see solace.game.Player
   */
  public boolean isCasting() {
    return casting;
  }

  /**
   * @see solace.game.Player
   */
  public void beginCasting(Clock.Event e) {
    casting = true;
    castingEvent = e;
  }

  /**
   * @see solace.game.Player
   */
  public void interruptCasting() {
    if (castingEvent != null) {
      castingEvent.cancel();
    }
    casting = false;
    sendln("Your spell was interrupted!");
  }

  /**
   * @see solace.game.Player
   */
  public void finishCasting() {
    casting = false;
    castingEvent = null;
  }
}
