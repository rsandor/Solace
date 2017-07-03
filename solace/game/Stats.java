package solace.game;

import java.util.*;

/**
 * Class used to generate various stats for characters, mobiles, items, etc.
 * in the game world. In short: this is the master mathematics engine behind the
 * entire game.
 *
 * The stats engine is designed to be customizable and tweakable via a long
 * list of parametric control constants. The base values for these constants
 * were chosen by constructing a rather elaborate spreadsheet in the Mac OSX
 * "Numbers" program.
 *
 * The spreadsheet used to construct the formulae and constants here is included
 * in the distrubution of this software and is entitled `spreadsheets/gamemath.numbers`.
 *
 * @author Ryan Sandor Richards
 */
public class Stats {
  public enum AbilityType { MAJOR, MINOR, TERTIARY };

  // Player character damage modifier parameters
  public static final double CH_DAMAGE_MOD_SCALE = 0.81;
  public static final double CH_DAMAGE_MOD_SHIFT = -1.0;

  // Player character hit modifier parameters
  public static final double CH_HIT_MOD_SCALE = 0.7;
  public static final double CH_HIT_MOD_SHIFT = 0;

  // Player character armor class parameters
  public static final double CH_AC_MOD_SCALE = 0.8;
  public static final double CH_AC_MOD_SHIFT = 0;
  public static final double CH_AC_MOD_SPEED_SCALE = 0.2;
  public static final double CH_AC_MOD_SPEED_POWER = 1.1205;

  // Player character ability score parameters
  public static final double CH_ABILITY_MAJOR_MINIMUM = 10.0;
  public static final double CH_ABILITY_LOG_BASE = 2.555;
  public static final double CH_ABILITY_MINOR_SCALAR = 0.7;
  public static final double CH_ABILITY_TERTIARY_SCALAR = 0.4;

  // Player character hit point parameters
  public static final double CH_HP_VITALITY_SCALE = 1.0;
  public static final double CH_HP_VITALITY_LOG_BASE = 2.1;
  public static final double CH_HP_STRENGTH_SCALE = 0.5;
  public static final double CH_HP_STRENGTH_LOG_BASE = 3.5;
  public static final double CH_HP_SHIFT = -3.0;

  // Player character magic point parameters
  public static final double CH_MP_MAGIC_SCALE = 1.1;
  public static final double CH_MP_MAGIC_LOG_BASE = 1.65;
  public static final double CH_MP_VITALITY_SCALE = 0.5;
  public static final double CH_MP_VITALITY_LOG_BASE = 4.0;
  public static final double CH_MP_SHIFT = -3.0;

  // Player character stamina point parameters
  public static final double CH_SP_SPEED_SCALE = 1.0;
  public static final double CH_SP_SPEED_LOG_BASE = 2.0;
  public static final double CH_SP_STRENGTH_SCALE = 0.9;
  public static final double CH_SP_STRENGTH_LOG_BASE = 2.8;
  public static final double CH_SP_SHIFT = 2;

  // Character saving throw parameters
  public static final double CH_SAVING_THROW_SCALAR = 0.21;
  public static final double CH_SAVING_THROW_LEVEL_POWER = 0.05;
  public static final Collection<String> CH_SAVING_THROW_NAMES =
    Collections.unmodifiableCollection(
      Arrays.asList(
        "will", "reflex", "resolve", "vigor", "prudence", "guile"
      )
    );

  // Mobile AC Parameters
  public static double MOB_AC_BASE = 4.0;
  public static double MOB_AC_SCALAR = 0.15;
  public static double MOB_AC_POWER_SCALE = 3.6;
  public static double MOB_AC_POWER_DIVISOR = 55.0;
  public static double MOB_AC_LEVEL_SCALE = 1.01;
  public static double MOB_AC_LEVEL_DIVSOR = 800;

  // Mobile HP Parameters
  public static double MOB_HP_SCALE = 0.85;
  public static double MOB_HP_LOG_BASE = 1.19;
  public static double MOB_HP_POWER_DIVISOR = 15.8;
  public static double MOB_HP_SHIFT = 10.0;
  public static double MOB_HP_POWER_EXPONENT = 1.7;

  // Mobile Attack Roll Parameters
  public static double MOB_ATTACK_ROLL_MIN = 0.4;
  public static double MOB_ATTACK_ROLL_MAX = 0.85;
  public static double MOB_ATTACK_ROLL_EXPONENT = 1.2;

  // Mobile Average Damage Parameters
  public static double MOB_DAMAGE_MAX_TO_KILL_PLAYER = 20.0;
  public static double MOB_DAMAGE_MIN_TO_KILL_PLAYER	= 6.0;
  public static double MOB_DAMAGE_EXPONENT = 1.3;

  // Armor Class Bases by Armor Type
  public static double ARMOR_BASE_AC_HEAD = 2.0;
  public static double ARMOR_BASE_AC_BODY = 5.0;
  public static double ARMOR_BASE_AC_HANDS = 1.0;
  public static double ARMOR_BASE_AC_LEGS = 3.0;
  public static double ARMOR_BASE_AC_WAIST = 1.0;
  public static double ARMOR_BASE_AC_FEET = 2.0;
  public static double ARMOR_BASE_AC_SHIELD = 3.0;

  // Armor Class Exponent by Level for all Armor Types
  public static double ARMOR_LEVEL_EXPONENT = 0.68;

  // Weapon parameters
  public static double WEAPON_CHANCE_TO_HIT_P35 = 0.65;
  public static double WEAPON_MIN_DAMAGE = 4.0;

  // Battle time estimates
  public static int BATTLE_TIME_P35 = 40;

  // Static caches
  protected static Hashtable<Integer, Double> cacheAverageHpByLevel =
    new Hashtable<Integer, Double>();

  /**
   * Determines a player character ability.
   *
   *    Major(level) = (level) * log_{logBase}(level) + majorMin
   *    Minor(level) = minorScale * Major(level)
   * Tertiary(level) = tertiaryScale * Major(level)
   *
   * With parameters:
   *
   *       logBase - Base of the log to use for the major formula
   *      majorMin - Minimum stat for the major ability score
   *    minorScale - How to scale the formula for minor abilities
   * tertiarySCale - How to scale the formulat for tertiary abilities
   *
   * @param p Player for which the ability will be determined.
   * @param name Name of the ability.
   */
  public static int getAbility(Player p, String name) {
    String major = p.getMajorStat();
    String minor = p.getMinorStat();
    AbilityType t = AbilityType.MAJOR;
    if (major.equals(name)) {
      t = AbilityType.MAJOR;
    }
    else if (minor.equals(name)) {
      t = AbilityType.MINOR;
    }
    else {
      t = AbilityType.TERTIARY;
    }
    return getAbility(p.getLevel(), t);
  }

  /**
   * Gets the value for a player character ability score at the given level with
   * the given ability type (major, minor, or tertiary).
   * @param level Level of the player character.
   * @param t Type of the ability (major, minor, or tertiary).
   * @return The standard value for the ability score.
   */
  public static int getAbility(int level, AbilityType t) {
    int ability = (int)Math.floor(CH_ABILITY_MAJOR_MINIMUM + level * (
      Math.log((double)level) / Math.log(CH_ABILITY_LOG_BASE)
    ));
    if (t == AbilityType.MAJOR) {
      return ability;
    }
    if (t == AbilityType.MINOR) {
      return (int)Math.floor(CH_ABILITY_MINOR_SCALAR * ability);
    }
    return (int)Math.floor(CH_ABILITY_TERTIARY_SCALAR * ability);
  }

  /**
   * Determines the damage modifier for given player character.
   * @param ch Character for which to determine the modifier.
   * @return The damage modifier.
   */
  public static int getDamageMod(solace.game.Character ch) {
    return getDamageMod(ch.getLevel());
  }

  /**
   * Calculates the damage modifier for a player character of the given level.
   * @param level Level of the player character.
   * @return The damage mod for that level.
   */
  public static int getDamageMod(int level) {
    return (int)(Math.pow(level, CH_DAMAGE_MOD_SCALE) + CH_DAMAGE_MOD_SHIFT);
  }

  /**
   * Determines the hit modifier for a given player character.
   * @param ch Character for which to determine the hit modifier.
   * @return The hit modifier.
   */
  public static int getHitMod(solace.game.Character ch) {
    return getHitMod(ch.getLevel());
  }

  /**
   * Determines the hit modifier for a player character of the given level.
   * @param  level Level of the player character.
   * @return       The character's hit modifier.
   */
  public static int getHitMod(int level) {
    return (int)(Math.pow(level, CH_HIT_MOD_SCALE) + CH_HIT_MOD_SHIFT);
  }

  /**
   * Determines the AC for the given player.
   * @param p Player for which to determine the armor class.
   * @return The armor class.
   */
  public static int getAC(Player p) {
    if (p.isMobile()) {
      Mobile m = (Mobile)p;
      return getMobileAC(m.getLevel(), m.getPower());
    }
    solace.game.Character c = (solace.game.Character)p;
    return getAC(c.getLevel(), c.getSpeed());
  }

  /**
   * Determines the AC for a mobile of the given level and power.
   * @param   level Level of the mobile.
   * @param   power Power of the mobile.
   * @return  The AC for the mobile.
   */
  public static int getMobileAC(int level, int power) {
    double ac = MOB_AC_BASE + MOB_AC_SCALAR * power *
      Math.pow(MOB_AC_POWER_SCALE, 1 + (level / MOB_AC_POWER_DIVISOR)) +
      MOB_AC_LEVEL_SCALE * Math.pow(level, 1 + (level / MOB_AC_LEVEL_DIVSOR));
    return (int)ac;
  }

  /**
   * Determines the armor class bonus modifier for a character of the given
   * level with the given speed statistics. This is determined by the following
   * formula:
   *
   *   Bonus(l, s) = a * s^x + l^y + A
   *
   * With parametric constants:
   *
   *   a - Speed scale     (CH_AC_MOD_SPEED_SCALE)
   *   x - Speed exponent  (CH_AC_MOD_SPEED_POWER)
   *   y - Level exponent  (CH_AC_MOD_SCALE)
   *   A - AC Shift        (CH_AC_MOD_SHIFT)
   *
   * @param level Level of the character.
   * @param speed Speed of the character.
   * @return The AC bonus.
   */
  public static int getAC(int level, int speed) {
    int speedAC = (int)Math.floor(CH_AC_MOD_SPEED_SCALE * Math.pow(
      speed,
      CH_AC_MOD_SPEED_POWER
    ));
    int levelAC = (int)(Math.pow(level, CH_AC_MOD_SCALE) + CH_AC_MOD_SHIFT);
    return levelAC + speedAC;
  }

  /**
   * Determines the maximum HP for a given player.
   * @param p Player for which to determin the maximum HP.
   * @return The maximum HP for the given player.
   */
  public static int getMaxHp(Player p) {
    if (p.isMobile()) {
      Mobile m = (Mobile)p;
      return getMobileMaxHP(m.getLevel(), m.getPower());
    }
    solace.game.Character c = (solace.game.Character)p;
    return getMaxHp(c.getVitality(), c.getStrength());
  }

  /**
   * Determines the maximum HP for a mobile of the given level and power.
   *
   * MobHP(L, P) = $scale ×
   *   (1 + P^$powerExponent / $powerDivisor) ×
   *   L × Log(L, $logBase) + $shift
   *
   * @param   level Level of the mobile.
   * @param   power Power of the mobile.
   * @return  The maximum HP for the mobile.
   */
  public static int getMobileMaxHP(int level, int power) {
    double hp =
      (1 + (Math.pow(power, MOB_HP_POWER_EXPONENT) / MOB_HP_POWER_DIVISOR)) *
      MOB_HP_SCALE *
      level *
      (Math.log(level) / Math.log(MOB_HP_LOG_BASE)) +
      MOB_HP_SHIFT;
    return (int)hp;
  }

  /**
   * Calculates the maximum HP for a player character with the given vitality
   * and strength. Maximum HP is determined by the following formula:
   *
   *   MaxHP(v, s) = a * v * log(v, x) + b * s * log(s, y) + H
   *
   * Using the parametric constants:
   *
   *   a - Vitality scale     (CH_HP_VITALITY_SCALE)
   *   x - Vitality log base  (CH_HP_VITALITY_LOG_BASE)
   *   b - Strength scale     (CH_HP_STRENGTH_SCALE)
   *   y - Strength log base  (CH_HP_STRENGTH_LOG_BASE)
   *   H - Hit point shift    (CH_HP_SHIFT)
   *
   * @param vitality Vitality ability score for the character.
   * @param strength Strength ability score for the character.
   * @return The maximum hit points for a the character.
   */
  public static int getMaxHp(int vitality, int strength) {
    double vitalityHP = CH_HP_VITALITY_SCALE * vitality *
      Math.log(vitality) / Math.log(CH_HP_VITALITY_LOG_BASE);
    double strengthHP = CH_HP_STRENGTH_SCALE * strength *
      Math.log(strength) / Math.log(CH_HP_STRENGTH_LOG_BASE);
    return (int)(Math.floor(vitalityHP + strengthHP) + CH_HP_SHIFT);
  }

  /**
   * Determines average hp for a player character at the given level.
   * @param level Level of the character.
   * @return The average hp for a player of the given level.
   */
  public static double getAvgHP(int level) {
    if (cacheAverageHpByLevel.containsKey(level)) {
      return cacheAverageHpByLevel.get(level);
    }
    int major = getAbility(level, AbilityType.MAJOR);
    int minor = getAbility(level, AbilityType.MINOR);
    int tertiary = getAbility(level, AbilityType.TERTIARY);
    double avg = (
      getMaxHp(major, minor) +
      getMaxHp(major, tertiary) +
      getMaxHp(minor, major) +
      getMaxHp(minor, tertiary) +
      getMaxHp(tertiary, major) +
      getMaxHp(tertiary, minor) +
      getMaxHp(tertiary, tertiary)
    ) / 7.0;
    cacheAverageHpByLevel.put(level, avg);
    return avg;
  }

  /**
   * Determines the maximum MP for a given player.
   *
   * MP = a × Mag × Log(Mag, m) + b × Vit × Log(Vit, v) + M
   *
   *   a - Magic scalar
   *   m - Magic log base
   *   b - Vitality scalar
   *   v - Vitality log base
   *   M - MP shift
   *
   * @param p Player for which to determine maximum MP.
   * @return The maximum MP.
   */
  public static int getMaxMp(Player p) {
    return getMaxMp(p.getMagic(), p.getVitality());
  }

  /**
   * Determines maximum MP for a player character by magic and vitality ability
   * scores.
   * @param   magic Magic ability score for the character.
   * @param   vitality Vitality ability score for the character.
   * @return  The maximum mp for the character.
   */
  public static int getMaxMp(int magic, int vitality) {
    double magicMP = CH_MP_MAGIC_SCALE * magic *
      Math.log(magic) / Math.log(CH_MP_MAGIC_LOG_BASE);
    double vitalityMP = CH_MP_VITALITY_SCALE * vitality *
      Math.log(vitality) / Math.log(CH_MP_VITALITY_LOG_BASE);
    return (int)(magicMP + vitalityMP + CH_MP_SHIFT);
  }

  /**
   * Determines the maximum SP for a given player.
   *
   * SP = a × Spe × Log(Spe, e) + b × Str × Log(Str, s) + S
   *
   *   a - Speed scalar
   *   e - Speed log base
   *   b - Strength scalar
   *   s - Strength log base
   *   S - SP Shift
   *
   * @param p Player for which to determine maximum SP.
   * @return The maximum SP.
   */
  public static int getMaxSp(Player p) {
    return getMaxSp(p.getSpeed(), p.getStrength());
  }

  /**
   * Determines a player character's maximum sp by speed and strength.
   * @param   speed Speed of the character.
   * @param   strength Strength of the character.
   * @return  The character's maximum sp.
   */
  public static int getMaxSp(int speed, int strength) {
    double speedSP = CH_SP_SPEED_SCALE * speed *
      Math.log(speed) / Math.log(CH_SP_SPEED_LOG_BASE);
    double strengthSP = CH_SP_STRENGTH_SCALE * strength *
      Math.log(strength) / Math.log(CH_SP_STRENGTH_LOG_BASE);
    return (int)(speedSP + strengthSP + CH_SP_SHIFT);
  }

  /**
   * Determines the saving throw of the given name for the given character.
   * There are six different saving throws, each calculated using two character
   * ability scores:
   *
   *   Str/Vit - Will
   *   Str/Spe - Reflex
   *   Str/Mag - Resolve
   *   Vit/Spe - Vigor
   *   Vit/Mag - Prudence
   *   Spe/Mag - Guile
   *
   * Each saving throw is determined via the following formula:
   *
   *   throw = s x (stat[a] + stat[b]) x (level ^ p)
   *
   * @param p Player for which to determine the saving throw.
   * @param name Name of the saving throw.
   * @return The value of the saving throw.
   * @throws InvalidSavingThrowException When given an invalid saving throw.
   */
  public static int getSavingThrow(Player p, String name)
    throws InvalidSavingThrowException
  {
    if (!CH_SAVING_THROW_NAMES.contains(name)) {
      throw new InvalidSavingThrowException(name);
    }

    double level = (double)p.getLevel();
    double a = 1, b = 1;

    if (name.equals("will")) {
      a = (double)p.getStrength();
      b = (double)p.getVitality();
    }
    else if (name.equals("reflex")) {
      a = (double)p.getStrength();
      b = (double)p.getSpeed();
    }
    else if (name.equals("resolve")) {
      a = (double)p.getStrength();
      b = (double)p.getMagic();
    }
    else if (name.equals("vigor")) {
      a = (double)p.getVitality();
      b = (double)p.getSpeed();
    }
    else if (name.equals("prudence")) {
      a = (double)p.getVitality();
      b = (double)p.getMagic();
    }
    else if (name.equals("guile")) {
      a = (double)p.getSpeed();
      b = (double)p.getMagic();
    }

    double savingThrow = CH_SAVING_THROW_SCALAR * (a + b) * (
      Math.pow(level, CH_SAVING_THROW_LEVEL_POWER)
    );
    return (int)savingThrow;
  }

  /**
   * Determines the roll against saving throw of the given name for the given
   * player.
   *
   * The roll against a saving throw for a player is determined by their own
   * value for that save. A higher will yield better results when rolling.
   *
   *   roll = save / 0.25
   *
   * @param p Player for which to determine the magic roll.
   * @param name Name of the saving throw.
   * @return The value of the saving throw.
   * @throws InvalidSavingThrowException When given an invalid saving throw.
   */
  public static int getMagicRoll(Player p, String name)
    throws InvalidSavingThrowException
  {
    if (!CH_SAVING_THROW_NAMES.contains(name)) {
      throw new InvalidSavingThrowException(name);
    }

    int save = 0;
    if (name.equals("will")) {
      save = p.getWillSave();
    } else if (name.equals("reflex")) {
      save = p.getReflexSave();
    } else if (name.equals("resolve")) {
      save = p.getResolveSave();
    } else if (name.equals("vigor")) {
      save = p.getVigorSave();
    } else if (name.equals("prudence")) {
      save = p.getPrudenceSave();
    } else if (name.equals("guile")) {
      save = p.getGuileSave();
    }

    return 4 * save;
  }

  /**
   * Determines a mobile's chance to hit a player of the same level at a given
   * mobile power. This is calculated using the following formula:
   *
   *   (M-m)*(power / 100)^k + m
   *
   * With the given parametric constants:
   *
   * 	 M - maximum chance to hit the player at the given power
   * 	 m - minimum chance to hit the player at the given power
   * 	 k - power curve shape exponent
   *
   * @param level Level of the mobile.
   * @param power Power of the mobile.
   * @return The mobile's chance to hit a player of the same level.
   */
  public static double getMobileChanceToHit(int power) {
    double max = MOB_ATTACK_ROLL_MAX;
    double min = MOB_ATTACK_ROLL_MIN;
    double p = MOB_ATTACK_ROLL_EXPONENT;
    return (max - min) * Math.pow(power/100.0, p) + min;
  }

  /**
   * Determines the attack roll for a mobile.
   * @param  m The mobile.
   * @return   The attack roll for the mobile.
   */
  public static int getAttackRoll(Mobile m) {
    return getMobileAttackRoll(m.getLevel(), m.getPower());
  }

  /**
   * Determines the attack roll for a mobile with the given level and power. The
   * attack roll is determined using the following formula:
   *
   *   Roll = AvgPlayerAC(level) / (1 - ChanceToHit(power))
   *
   * @param level Level of the mobile.
   * @param power Power of the mobile.
   * @return The mobile's attack roll.
   */
  public static int getMobileAttackRoll(int level, int power) {
    double ac = getAverageACByLevel(level);
    double chance = getMobileChanceToHit(power);

    //System.out.format("\t%.2f %.2f\n", ac, chance);

    return (int)((double)ac / (1 - chance));
  }

  /**
   * Determines the average damage a mobile inflicts on a successful attack.
   * @param  m The mobile.
   * @return   The average damage the mobile inflicts.
   */
  public static int getAverageDamage(Mobile m) {
    return getMobileAverageDamage(m.getLevel(), m.getPower());
  }

  /**
   * Determines the average damage a mobile inflicts given its level and power.
   * The following formula is used to determine the damage:
   *
   *   damage = CEIL(
	 *     AvgPlayerHP[l] / [(M-m) × [1-(p/100)^k] + m]]
   *   )
   *
   * With the following parametric constants:
   *
   *   M - Maximum number of hits a player should take before death
   *   m - Minimum number of hits a player should take before death
   *   k - Power curve exponent (shape)
   *
   * @param   level Level of the mobile.
   * @param   power Power of the mobile.
   * @return  The average damage the mobile inflicts.
   */
  public static int getMobileAverageDamage(int level, int power) {
    double hp = getAvgHP(level);
    double chance = getMobileChanceToHit(power);
    double M = MOB_DAMAGE_MAX_TO_KILL_PLAYER;
    double m = MOB_DAMAGE_MIN_TO_KILL_PLAYER;
    double k = MOB_DAMAGE_EXPONENT;
    int result = (int)Math.ceil(
      (hp / ((M - m) * (1 - Math.pow(((double)power/100.0), k)) + m))
    );
    return result;
  }

  /**
   * Determines the base AC for a piece of armor of the given slot at the given
   * level. The base AC acts as a mean for equipment and can be used to generate
   * equipment loot ranging from the mundane to the truly extraordinary.
   * @param level Level of the armor.
   * @param slot Slot for the armor.
   * @return The armor's base AC bonus.
   */
  public static int getArmorBaseAC(int level, String slot) {
    double base = 0;
    if (slot.equals("head")) {
      base = ARMOR_BASE_AC_HEAD;
    }
    else if (slot.equals("body")) {
      base = ARMOR_BASE_AC_BODY;
    }
    else if (slot.equals("hands")) {
      base = ARMOR_BASE_AC_HANDS;
    }
    else if (slot.equals("legs")) {
      base = ARMOR_BASE_AC_LEGS;
    }
    else if (slot.equals("waist")) {
      base = ARMOR_BASE_AC_WAIST;
    }
    else if (slot.equals("feet")) {
      base = ARMOR_BASE_AC_FEET;
    }
    else if (slot.equals("off-hand")) {
      base = ARMOR_BASE_AC_SHIELD;
    }
    return (int)(base * Math.pow(level, ARMOR_LEVEL_EXPONENT));
  }

  /**
   * Determines the expected average AC for a fully equipped character at the
   * given level. Assumes a minor speed attribute bonus to the AC modifier.
   * @param level Level of the character.
   * @return Expected average AC for a character of the given level.
   */
  public static double getAverageACByLevel(int level) {
    String[] slots = new String[] {
      "head", "body", "hands", "legs", "waist", "feet", "off-hand"
    };
    int ac = 0;
    for (String slot : slots) {
      ac += getArmorBaseAC(level, slot);
    }
    return ac + getAC(level, getAbility(level, AbilityType.MINOR));
  }

  /**
   * Determines the base attack roll for a weapon.
   * @param level Level of the weapon.
   * @return The base attack roll for a weapon of the given level.
   */
  public static int getWeaponAttackRoll(int level) {
    int mobAc = getMobileAC(level, 35);
    int hitMod = getHitMod(level);
    return (int)((mobAc - hitMod) / (1.0 - WEAPON_CHANCE_TO_HIT_P35));
  }

  /**
   * Determines the average damage for a weapon of the given level.
   * @param  level Level of the weapon.
   * @return       Average damage for the weapon.
   */
  public static int getWeaponAverageDamage(int level) {
    int mobHP = getMobileMaxHP(level, 35);

    int result = (int)Math.ceil(
      WEAPON_MIN_DAMAGE + (
        (1 / WEAPON_CHANCE_TO_HIT_P35) * mobHP / (BATTLE_TIME_P35 / 2)
      )
    );

    return result;
  }
}
