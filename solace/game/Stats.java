package solace.game;

import java.util.*;

/**
 * Class used to generate various stats for characters, mobiles, items, etc.
 * in the game world. This class gives the game's combat it's intrinsic feel
 * and is used to determine everything from overall power level for mobiles
 * to base statistics for player characters.
 *
 * On Mac OSX you can view tables representing the formulas used in this class
 * by opening the `gamemath.numbers` file in the program Numbers.
 *
 * @author Ryan Sandor Richards
 */
public class Stats {
  // Player character damage modifier parameters
  public static final double CH_DAMAGE_MOD_SCALE = 0.81;
  public static final double CH_DAMAGE_MOD_SHIFT = -1.0;

  // Player character hit modifier parameters
  public static final double CH_HIT_MOD_SCALE = 0.7;
  public static final double CH_HIT_MOD_SHIFT = 0;

  // Player character armor class parameters
  public static final double CH_AC_MOD_SCALE = 0.8;
  public static final double CH_AC_MOD_SHIFT = 0;
  public static final double CH_AC_SPEED_SCALE = 0.2;
  public static final double CH_AC_SPEED_POWER = 1.1205;

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
  public static final double CH_MP_SHIFT = 0;

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
   * @param ch Character for which the ability will be determined.
   * @param name Name of the ability.
   */
  public static int getAbility(solace.game.Character ch, String name) {
    double level = (double)ch.getLevel();
    String major = ch.getMajorStat();
    String minor = ch.getMinorStat();

    double ability = CH_ABILITY_MAJOR_MINIMUM + level * (
      Math.log((double)level) / Math.log(CH_ABILITY_LOG_BASE)
    );

    if (major.equals(name)) {
      return (int)ability;
    }
    if (minor.equals(name)) {
      return (int)(CH_ABILITY_MINOR_SCALAR * ability);
    }
    return (int)(CH_ABILITY_TERTIARY_SCALAR * ability);
  }

  /**
   * Determines the damage modifier for given player character.
   * @param ch Character for which to determine the modifier.
   * @return The damage modifier.
   */
  public static int getDamageMod(solace.game.Character ch) {
    double level = ch.getLevel();
    double damageMod = Math.pow(level, CH_DAMAGE_MOD_SCALE) +
      CH_DAMAGE_MOD_SHIFT;
    return (int)damageMod;
  }

  /**
   * Determines the hit modifier for a given player character.
   * @param ch Character for which to determine the hit modifier.
   * @return The hit modifier.
   */
  public static int getHitMod(solace.game.Character ch) {
    double level = ch.getLevel();
    double hitModifier = Math.pow(level, CH_HIT_MOD_SCALE) + CH_HIT_MOD_SHIFT;
    return (int)hitModifier;
  }

  /**
   * Determines the armor class for a given character.
   * @param  ch Character for which to determine the armor class.
   * @return The armor class for the character.
   */
  public static int getAC(solace.game.Character ch) {
    double level = (double)ch.getLevel();
    double speed = (double)ch.getSpeed();
    double speedAC = CH_AC_SPEED_SCALE * Math.pow(speed, CH_AC_SPEED_POWER);
    double levelAC = Math.pow(level, CH_AC_MOD_SHIFT) + CH_AC_MOD_SHIFT;
    return (int)(levelAC + speedAC);
  }

  /**
   * Determines the maximum HP for a given character.
   * @param ch Character for which to determine maximum HP.
   * @return The maximum HP.
   */
  public static int getMaxHp(solace.game.Character ch) {
    double vitality = (double)ch.getVitality();
    double strength = (double)ch.getStrength();
    double vitalityHP = CH_HP_VITALITY_SCALE * vitality *
      Math.log(vitality) / Math.log(CH_HP_VITALITY_LOG_BASE);
    double strengthHP = CH_HP_STRENGTH_SCALE * strength *
      Math.log(strength) / Math.log(CH_HP_STRENGTH_LOG_BASE);
    return (int)(vitalityHP + strengthHP + CH_HP_SHIFT);
  }

  /**
   * Determines the maximum MP for a given character.
   *
   * MP = a × Mag × Log(Mag, m) + b × Vit × Log(Vit, v) + M
   *
   *   a - Magic scalar
   *   m - Magic log base
   *   b - Vitality scalar
   *   v - Vitality log base
   *   M - MP shift
   *
   * @param ch Character for which to determine maximum MP.
   * @return The maximum MP.
   */
  public static int getMaxMp(solace.game.Character ch) {
    double magic = (double)ch.getMagic();
    double vitality = (double)ch.getVitality();
    double magicMP = CH_MP_MAGIC_SCALE * magic *
      Math.log(magic) / Math.log(CH_MP_MAGIC_LOG_BASE);
    double vitalityMP = CH_MP_VITALITY_SCALE * vitality *
      Math.log(vitality) / Math.log(CH_MP_VITALITY_LOG_BASE);
    return (int)(magicMP + vitalityMP + CH_MP_SHIFT);
  }

  /**
   * Determines the maximum SP for a given character.
   *
   * SP = a × Spe × Log(Spe, e) + b × Str × Log(Str, s) + S
   *
   *   a - Speed scalar
   *   e - Speed log base
   *   b - Strength scalar
   *   s - Strength log base
   *   S - SP Shift
   *
   * @param ch Character for which to determine maximum SP.
   * @return The maximum SP.
   */
  public static int getMaxSp(solace.game.Character ch) {
    double speed = (double)ch.getSpeed();
    double strength = (double)ch.getStrength();
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
   * @param ch Character for which to determine the saving throw.
   * @param name Name of the saving throw.
   * @return The value of the saving throw.
   */
  public static int getSavingThrow(solace.game.Character ch, String name)
    throws InvalidSavingThrowException
  {
    if (!CH_SAVING_THROW_NAMES.contains(name)) {
      throw new InvalidSavingThrowException(name);
    }

    double level = (double)ch.getLevel();
    double a = 1, b = 1;

    if (name.equals("will")) {
      a = (double)ch.getStrength();
      b = (double)ch.getVitality();
    }
    else if (name.equals("reflex")) {
      a = (double)ch.getStrength();
      b = (double)ch.getSpeed();
    }
    else if (name.equals("resolve")) {
      a = (double)ch.getStrength();
      b = (double)ch.getMagic();
    }
    else if (name.equals("vigor")) {
      a = (double)ch.getVitality();
      b = (double)ch.getSpeed();
    }
    else if (name.equals("prudence")) {
      a = (double)ch.getVitality();
      b = (double)ch.getMagic();
    }
    else if (name.equals("guile")) {
      a = (double)ch.getSpeed();
      b = (double)ch.getMagic();
    }

    double savingThrow = CH_SAVING_THROW_SCALAR * (a + b) * (
      Math.pow(level, CH_SAVING_THROW_LEVEL_POWER)
    );
    return (int)savingThrow;
  }

  /**
   * Determines the maximum HP for a given mobile.
   *
   * MobHP(L, P) = $scale ×
   *   (1 + P^$powerExponent / $powerDivisor) ×
   *   L × Log(L, $logBase) + $shift
   *
   * @param m Mobile for which to determin the maximum HP.
   * @return The maximum HP for the given mobile.
   */
  public static int getMaxHp(Mobile m) {
    double level = (double)m.getLevel();
    double power = (double)m.getPower();
    double hp = MOB_HP_SHIFT + MOB_HP_SCALE * (
      ((1 + Math.pow(power, MOB_HP_POWER_EXPONENT)) / MOB_HP_POWER_DIVISOR) *
      level * Math.log(level) / Math.log(MOB_HP_LOG_BASE)
    );
    return (int)hp;
  }

  /**
   * Determines the AC for the given mobile.
   *
   * mobAC(L,P) = $base + $scalar × [
   *    $powerScale × P^(1 + L ÷ $powerDivisor) +
   *    $levelScale × L^(1 + L ÷ $levelDivisor)
   * ]
   *
   * @param m Mobile for which to determine the armor class.
   * @return The armor class of the mobile.
   */
  public static int getAC(Mobile m) {
    double level = (double)m.getLevel();
    double power = (double)m.getPower();
    double ac = MOB_AC_BASE + MOB_AC_SCALAR * (
      MOB_AC_POWER_SCALE * Math.pow(power, 1 + (level / MOB_AC_POWER_DIVISOR)) +
      MOB_AC_LEVEL_SCALE * Math.pow(level, 1 + (level / MOB_AC_LEVEL_DIVSOR))
    );
    return (int)ac;
  }

}
