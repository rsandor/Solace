package solace.game;

import solace.game.effect.PlayerEffect;
import solace.game.effect.PlayerModifier;
import solace.game.effect.PlayerModifierIdentity;
import solace.game.effect.ScriptedPlayerEffect;
import solace.script.PassiveNotFoundException;
import solace.script.ScriptedPassives;
import solace.util.Clock;
import solace.util.Log;
import solace.io.Buffs;
import solace.net.Connection;
import java.util.*;

/**
 * Implements common functionality of the player interface shared by both
 * players and mobiles.
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
    CooldownTimer(int d) {
      usedAt = new Date();
      duration = (long)d;
    }

    /**
     * @return The time remaining, in seconds, until the cool down is complete.
     */
    int getTimeRemaining() {
      long elapsed = new Date().getTime() - usedAt.getTime();
      elapsed /= 1000;
      return (int)Math.max(0, duration - elapsed);
    }
  }

  // Instance variables
  private boolean immortal = false;
  PlayState state = PlayState.STANDING;
  Room room = null;
  int level;
  int hp;
  int mp;
  int sp;
  String majorStat = "none";
  String minorStat = "none";

  private boolean onGCDCooldown = false;
  private String comboAction;
  private boolean casting = false;
  private Clock.Event castingEvent;

  private Hashtable<String, CooldownTimer> cooldownTimers = new Hashtable<>();
  private Hashtable<String, Integer> passiveLevels = new Hashtable<>();
  private Hashtable<String, Integer> cooldowns = new Hashtable<>();
  private final Hashtable<String, Buff> buffs = new Hashtable<>();

  // Abstract Player MethodPassives
  public abstract void die(Player killer);
  public abstract boolean isMobile();
  public abstract void sendMessage(String s);
  public abstract void send(String msg);
  public abstract void sendln(String msg);
  public abstract void sendln(String... lines);
  public abstract void wrapln(String msg);
  public abstract String getName();
  public abstract boolean hasName(String namePrefix);
  public abstract String getDescription();
  public abstract int getAttackRoll();
  public abstract int getHitMod();
  public abstract int getDamageMod();
  public abstract int getAverageDamage();
  public abstract int getNumberOfAttacks();
  public abstract Connection getConnection();
  public abstract int getWeaponProficiency(String name);
  public abstract Set<DamageType> getBaseAttackDamageTypes();

  /**
   * Sets the passives and cooldowns for this character. This method should be
   * called after loading a character, on level, skill level, or when acquiring
   * a new skill.
   */
  public void setPassivesAndCooldowns() {
    passiveLevels.clear();
    cooldowns.clear();
  }

  /**
   * Determines the ability score of the given name.
   * @param name Name of the ability score.
   * @return The ability score for this player.
   * @see solace.game.Stats
   */
  protected int getAbility(String name) {
    double score = (double)Stats.getAbility(this, name);
    for (PlayerEffect effect : getEffects()) {
      PlayerModifier<Double> modifier;
      switch (name) {
        case "strength": modifier = effect.getModStrength(); break;
        case "vitality": modifier = effect.getModVitality(); break;
        case "magic": modifier = effect.getModMagic(); break;
        case "speed": modifier = effect.getModSpeed(); break;
        default: modifier = new PlayerModifierIdentity<>();
      }
      score = modifier.modify(this, score);
    }
    return (int)Math.round(score);
  }

  /**
   * Gets the maximum value for the given resource.
   * @param name Name of the resource.
   * @return The maximum value of the resource for this player, or -1 if the
   *   provided resource name is invalid.
   */
  protected int getMaxResource(String name) {
    switch (name) {
      case "hp": return Stats.getMaxHp(this);
      case "mp": return Stats.getMaxMp(this);
      case "sp": return Stats.getMaxSp(this);
    }
    return -1;
  }

  /**
   * Sets a passive on the player for the given name at the given level.
   * @param name Name of the passive.
   * @param level Level of the passive.
   */
  void setPassive(String name, int level) {
    if (!ScriptedPassives.has(name)) {
      Log.warn(String.format(
        "Unable to find passive with name '%s' for player '%s'", name, getName()));
    }
    passiveLevels.put(name, level);
  }

  @Override
  public int getPassiveLevel(String name) {
    if (passiveLevels.keySet().contains(name)) {
      return passiveLevels.get(name);
    }
    return -1;
  }

  /**
   * Sets a cooldown on the player for the given name at the given level.
   * @param name Name of the cooldown.
   * @param level Level of the cooldown.
   */
  void setCooldown(String name, int level) {
    cooldowns.put(name, level);
  }

  /**
   * Sends messages to player when a buff is applied.
   * @param b Buff begin applied.
   */
  private void sendBuffBeginMessages(Buff b) {
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
  private void sendBuffEndMessages(Buff b) {
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

  @Override
  public solace.game.Character getCharacter() { return Character.NULL; }

  @Override
  public Account getAccount() { return Account.NULL; }

  @Override
  public int getSavingThrow(String name) {
    try {
      return Stats.getSavingThrow(this, name);
    } catch (InvalidSavingThrowException e) {
      Log.error(String.format(
        "Invalid saving throw name encountered: %s", name));
    }
    return 0;
  }

  @Override
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

  @Override
  public PlayState getPlayState() { return state; }

  @Override
  public void setPlayState(PlayState s) { state = s; }

  @Override
  public boolean isSleeping() { return state == PlayState.SLEEPING; }

  @Override
  public void setSleeping() { state = PlayState.SLEEPING; }

  @Override
  public boolean isResting() { return state == PlayState.RESTING; }

  @Override
  public void setResting() { state = PlayState.RESTING; }

  @Override
  public boolean isSitting() { return state == PlayState.SITTING; }

  @Override
  public void setSitting() { state = PlayState.SITTING; }

  @Override
  public boolean isRestingOrSitting() { return state == PlayState.RESTING || state == PlayState.SITTING; }

  @Override
  public boolean isStanding() { return state == PlayState.STANDING; }

  @Override
  public void setStanding() { state = PlayState.STANDING; }

  @Override
  public boolean isFighting() { return state == PlayState.FIGHTING; }

  @Override
  public void setFighting() { state = PlayState.FIGHTING; }

  @Override
  public boolean isStandingOrFighting() { return state == PlayState.FIGHTING || state == PlayState.STANDING; }

  @Override
  public Room getRoom() { return room; }

  @Override
  public void setRoom(Room r) { room = r; }

  @Override
  public int getLevel() { return level; }

  @Override
  public void setLevel(int l) { level = l; }

  @Override
  public void setMajorStat(String name) { majorStat = name; }

  @Override
  public void setMinorStat(String name) { minorStat = name; }

  @Override
  public String getMajorStat() { return majorStat; }

  @Override
  public String getMinorStat() { return minorStat; }

  @Override
  public int getHp() { return hp; }

  @Override
  public void setHp(int v) { hp = v; }

  @Override
  public int getMaxHp() {
    return getMaxResource("hp");
  }

  @Override
  public int getMp() { return mp; }

  @Override
  public void setMp(int v) { mp = v; }

  @Override
  public int getMaxMp() { return getMaxResource("mp"); }

  @Override
  public int getSp() { return sp; }

  @Override
  public void setSp(int v) { sp = v; }

  @Override
  public int getMaxSp() { return getMaxResource("sp"); }

  @Override
  public int getStrength() { return getAbility("strength"); }

  @Override
  public int getVitality() { return getAbility("vitality"); }

  @Override
  public int getMagic() { return getAbility("magic"); }

  @Override
  public int getSpeed() { return getAbility("speed"); }

  @Override
  public int getWillSave() { return getSavingThrow("will"); }

  @Override
  public int getReflexSave() { return getSavingThrow("reflex"); }

  @Override
  public int getResolveSave() { return getSavingThrow("resolve"); }

  @Override
  public int getVigorSave() { return getSavingThrow("vigor"); }

  @Override
  public int getPrudenceSave() { return getSavingThrow("prudence"); }

  @Override
  public int getGuileSave() { return getSavingThrow("guile"); }

  @Override
  public int getAC() { return Stats.getAC(this); }

  @Override
  public int applyDamage(Damage d) {
    // TODO Engine Hook: Damage
    hp -= (int)d.getAmount();
    return (int)d.getAmount();
  }

  @Override
  public boolean isDead() {
    return getHp() <= 0;
  }

  @Override
  public boolean isOnGCD() {
    return onGCDCooldown;
  }

  @Override
  public void setOnGCD() {
    onGCDCooldown = true;
    // TODO GCD cooldowns should really be independent of the global clock...
    Clock.getInstance().schedule(
      String.format("GCD for %s", getName()),
      2, () -> onGCDCooldown = false);
  }

  @Override
  public void setOnCooldown(String name, int duration) {
    Log.trace(String.format("Setting cooldown at %d for %s", duration, name));
    cooldownTimers.put(name, new CooldownTimer(duration));
  }

  @Override
  public boolean isOnCooldown(String name) {
    if (!cooldownTimers.containsKey(name)) {
      return false;
    }
    return cooldownTimers.get(name).getTimeRemaining() > 0;
  }

  @Override
  public int getCooldownDuration(String name) {
    if (cooldownTimers.containsKey(name)) {
      return cooldownTimers.get(name).getTimeRemaining();
    }
    return -1;
  }

  @Override
  public void setComboAction(String action) { comboAction = action; }

  @Override
  public String getComboAction() { return comboAction == null ? "" : comboAction; }

  @Override
  public boolean hasPassive(String name) { return passiveLevels.containsKey(name); }

  @Override
  public Collection<Passive> getPassives() {
    List<Passive> passives = new LinkedList<>();
    for (String name : passiveLevels.keySet()) {
      try {
        passives.add(ScriptedPassives.get(name));
      } catch (PassiveNotFoundException e) {
        Log.warn(String.format(
          "Encountered unknown passive '%s' for player '%s'", name, getName()));
      }
    }
    return Collections.unmodifiableCollection(passives);
  }

  @Override
  public Collection<PlayerEffect> getEffects() {
    List<PlayerEffect> effects = new LinkedList<>();
    for (Passive p : getPassives()) {
      effects.add(p.getEffect());
    }
    return Collections.unmodifiableCollection(effects);
  }

  @Override
  public boolean hasCooldown(String name) { return cooldowns.containsKey(name); }

  @Override
  public int getCooldownLevel(String name) {
    if (!hasCooldown(name)) return -1;
    return cooldowns.get(name);
  }

  @Override
  public Collection<String> getCooldowns() { return Collections.unmodifiableCollection(cooldowns.keySet()); }

  @Override
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

  @Override
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

  @Override
  public boolean hasBuff(String name) { return buffs.containsKey(name); }

  @Override
  public Buff getBuff(String name) { return buffs.get(name); }

  @Override
  public void removeBuff(String name) {
    if (!hasBuff(name)) {
      return;
    }
    Buff b = getBuff(name);
    b.cancelTickAction();
    buffs.remove(name);
    sendBuffEndMessages(b);
  }

  @Override
  public Collection<Buff> getBuffs() {
    synchronized (buffs) {
      List<Buff> remove = new LinkedList<>();
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

  @Override
  public void removeExpiredBuffs() {
    synchronized (buffs) {
      for (Buff b : buffs.values()) {
        if (b.hasExpired()) {
          removeBuff(b.getName());
        }
      }
    }
  }

  @Override
  public void removeAllBuffs() {
    synchronized (buffs) {
      for (Buff b : buffs.values()) {
        removeBuff(b.getName());
      }
    }
  }

  @Override
  public boolean isImmortal() {
    return immortal;
  }

  @Override
  public void toggleImmortal(Player setter) {
    setImmortal(!immortal, setter);
  }

  @Override
  public void setImmortal(boolean i) {
    immortal = i;
  }

  @Override
  public void setImmortal(boolean i, Player setter) {
    immortal = i;
    if (immortal) {
      Log.warn(String.format(
        "Player '%s' has been set as immortal by player '%s'",
        getName(), setter.getName()));
      sendMessage("You have been granted immortality! You are now impervious to damage!");
      setter.sendMessage(String.format("%s is now immortal.", getName()));
    } else {
      Log.warn(String.format(
        "Player '%s' has been has been set as NOT immortal by player '%s'",
        getName(), setter.getName()));
      sendMessage("You are no longer immortal! Damage now applies as normal");
      setter.sendMessage(String.format("%s is no longer immortal.", getName()));
    }
  }

  @Override
  public boolean isVisibleTo(Player viewer) {
    // TODO Add a special buff that allows players to see "vanished" players.
    //      This should not be easy to attain for player players, but some
    //      "god" level mobs should always have the buff.
    return !hasBuff("vanished");
  }

  @Override
  public void resetVisibilityOnAction(String event) {
    // TODO Various buffs need to be added to make this implementation more interesting.
    //      For the time being basically everything that can drop the "vanished" buff will
    //      do so (helps us avoid having to fix all messaging around movement dropping
    //      items, etc., for instance: "Someone moves west..." or simply omitting the
    //      message at all, etc.).
    if (hasBuff("vanished")) {
      removeBuff("vanished");
    }
  }

  @Override
  public boolean isCasting() {
    return casting;
  }

  @Override
  public void beginCasting(Clock.Event e) {
    casting = true;
    castingEvent = e;
  }

  @Override
  public void interruptCasting() {
    if (castingEvent != null) {
      castingEvent.cancel();
    }
    casting = false;
    sendln("Your spell was interrupted!");
  }

  @Override
  public void finishCasting() {
    casting = false;
    castingEvent = null;
  }

  @Override
  public Battle getBattle() { return BattleManager.getBattleFor(this); }

  @Override
  public void applyDot(String name, int avg, int d, int f, String msg) {
    applyBuff(new DotBuff(name, this, avg, d, f, msg));
  }
}
