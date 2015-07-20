package game;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import solace.game.Stats;
import solace.game.Character;
import solace.game.Mobile;
import java.util.Hashtable;
import java.util.LinkedList;

public class StatsTest {
  solace.game.Character ch;
  Mobile mob;

  @Before
  public void createCharacter() {
    ch = new solace.game.Character("example");
    ch.setLevel(50);
    ch.setMajorStat("strength");
    ch.setMinorStat("magic");
  }

  @Before
  public void createMobileMock() {
    mob = mock(Mobile.class);
    when(mob.getLevel()).thenReturn(50);
    when(mob.getPower()).thenReturn(50);
  }

  @Test
  public void characterAbilityScores() {
    assertEquals(218, Stats.getAbility(ch, "strength"));
    assertEquals(87, Stats.getAbility(ch, "vitality"));
    assertEquals(152, Stats.getAbility(ch, "magic"));
    assertEquals(87, Stats.getAbility(ch, "speed"));
  }

  @Test
  public void majorAbilityScores() {
    assertEquals(10, Stats.getAbility(1, Stats.AbilityType.MAJOR));
    assertEquals(65, Stats.getAbility(18, Stats.AbilityType.MAJOR));
    assertEquals(218, Stats.getAbility(50, Stats.AbilityType.MAJOR));
    assertEquals(372, Stats.getAbility(78, Stats.AbilityType.MAJOR));
    assertEquals(500, Stats.getAbility(100, Stats.AbilityType.MAJOR));
  }

  @Test
  public void minorAbilityScores() {
    assertEquals(7, Stats.getAbility(1, Stats.AbilityType.MINOR));
    assertEquals(66, Stats.getAbility(25, Stats.AbilityType.MINOR));
    assertEquals(152, Stats.getAbility(50, Stats.AbilityType.MINOR));
    assertEquals(248, Stats.getAbility(75, Stats.AbilityType.MINOR));
    assertEquals(350, Stats.getAbility(100, Stats.AbilityType.MINOR));
  }

  @Test
  public void tertiaryAbilityScores() {
    assertEquals(4, Stats.getAbility(1, Stats.AbilityType.TERTIARY));
    assertEquals(38, Stats.getAbility(25, Stats.AbilityType.TERTIARY));
    assertEquals(87, Stats.getAbility(50, Stats.AbilityType.TERTIARY));
    assertEquals(142, Stats.getAbility(75, Stats.AbilityType.TERTIARY));
    assertEquals(200, Stats.getAbility(100, Stats.AbilityType.TERTIARY));
  }

  @Test
  public void characterDamageMod() {
    assertEquals(22, Stats.getDamageMod(ch));
  }

  @Test
  public void damageModByLevel() {
    assertEquals(0, Stats.getDamageMod(1));
    assertEquals(12, Stats.getDamageMod(25));
    assertEquals(22, Stats.getDamageMod(50));
    assertEquals(32, Stats.getDamageMod(75));
    assertEquals(40, Stats.getDamageMod(100));
  }

  @Test
  public void characterHitMod() {
    assertEquals(15, Stats.getHitMod(ch));
  }

  @Test
  public void hitModByLevel() {
    assertEquals(1, Stats.getHitMod(1));
    assertEquals(9, Stats.getHitMod(25));
    assertEquals(15, Stats.getHitMod(50));
    assertEquals(20, Stats.getHitMod(75));
    assertEquals(25, Stats.getHitMod(100));
  }

  @Test
  public void characterAC() {
    assertEquals(51, Stats.getAC(ch));
  }

  @Test
  public void getACByLevelAndSpeed() {
    assertEquals(3, Stats.getAC(1, Stats.getAbility(1, Stats.AbilityType.MAJOR)));
    assertEquals(2, Stats.getAC(1, Stats.getAbility(1, Stats.AbilityType.MINOR)));
    assertEquals(1, Stats.getAC(1, Stats.getAbility(1, Stats.AbilityType.TERTIARY)));

    assertEquals(45, Stats.getAC(25, Stats.getAbility(25, Stats.AbilityType.MAJOR)));
    assertEquals(34, Stats.getAC(25, Stats.getAbility(25, Stats.AbilityType.MINOR)));
    assertEquals(24, Stats.getAC(25, Stats.getAbility(25, Stats.AbilityType.TERTIARY)));

    assertEquals(105, Stats.getAC(50, Stats.getAbility(50, Stats.AbilityType.MAJOR)));
    assertEquals(77, Stats.getAC(50, Stats.getAbility(50, Stats.AbilityType.MINOR)));
    assertEquals(51, Stats.getAC(50, Stats.getAbility(50, Stats.AbilityType.TERTIARY)));

    assertEquals(175, Stats.getAC(75, Stats.getAbility(75, Stats.AbilityType.MAJOR)));
    assertEquals(127, Stats.getAC(75, Stats.getAbility(75, Stats.AbilityType.MINOR)));
    assertEquals(82, Stats.getAC(75, Stats.getAbility(75, Stats.AbilityType.TERTIARY)));

    assertEquals(250, Stats.getAC(100, Stats.getAbility(100, Stats.AbilityType.MAJOR)));
    assertEquals(180, Stats.getAC(100, Stats.getAbility(100, Stats.AbilityType.MINOR)));
    assertEquals(114, Stats.getAC(100, Stats.getAbility(100, Stats.AbilityType.TERTIARY)));
  }

  @Test
  public void maxHPByCharacter() {
    assertEquals(989, Stats.getMaxHp(ch));
    ch.setMajorStat("vitality");
    ch.setMinorStat("strength");
    assertEquals(1883, Stats.getMaxHp(ch));
  }

  @Test
  public void maxHPByVitalityAndStrength() {
    int[][] expected = new int[][] {
      { 33, 24, 30, 13, 17, 9, 6 },
      { 690, 542, 635, 355, 424, 293, 238 },
      { 1883, 1494, 1734, 989, 1181, 825, 675 },
      { 3352, 2671, 3087, 1777, 2120, 1491, 1226 },
      { 5003, 4000, 4608, 2665, 3183, 2243, 1848 }
    };
    int[] levels = new int[] { 1, 25, 50, 75, 100 };

    for (int i = 0; i < levels.length; i++) {
      int major = Stats.getAbility(levels[i], Stats.AbilityType.MAJOR);
      int minor = Stats.getAbility(levels[i], Stats.AbilityType.MINOR);
      int tertiary = Stats.getAbility(levels[i], Stats.AbilityType.TERTIARY);

      int[][] args = new int[][] {
        { major, minor },
        { minor, major },
        { major, tertiary },
        { tertiary, major },
        { minor, tertiary },
        { tertiary, minor },
        { tertiary, tertiary }
      };

      for (int k = 0; k < args.length; k++) {
        int vit = args[k][0];
        int str = args[k][1];
        assertEquals(expected[i][k], Stats.getMaxHp(vit, str));
      }
    }
  }

  @Test
  public void averageHPByLevel() {
    int[] levels = new int[] { 1, 25, 50, 75, 100 };
    double[] expectedAverages = new double[] {
      18.86, 453.86, 1254.43, 2246.29, 3364.29
    };
    for (int k = 0; k < levels.length; k++) {
      int level = levels[k];
      double expected = expectedAverages[k];
      assertEquals(expected, Stats.getAvgHP(level), 0.01);
    }
  }

  @Test
  public void maxMpByCharacter() {
    assertEquals(1814, Stats.getMaxMp(ch));
  }

  @Test
  public void maxMpByMagicAndVitality() {
    int[][] expected = new int[][] {
      { 52, 49, 35, 28, 17, 14, 11 },
      { 1047,	997,	760,	654, 456,	400, 350 },
      { 2850, 2715, 2097, 1814, 1273, 1125, 990 },
      { 5069, 4829, 3752, 3254, 2294, 2035, 1796 },
      { 7561, 7204, 5621, 4882, 3445, 3064, 2706}
    };
    int[] levels = new int[] { 1, 25, 50, 75, 100 };

    for (int i = 0; i < levels.length; i++) {
      int major = Stats.getAbility(levels[i], Stats.AbilityType.MAJOR);
      int minor = Stats.getAbility(levels[i], Stats.AbilityType.MINOR);
      int tertiary = Stats.getAbility(levels[i], Stats.AbilityType.TERTIARY);

      int[][] args = new int[][] {
        { major, minor },
        { major, tertiary },
        { minor, major },
        { minor, tertiary },
        { tertiary, major },
        { tertiary, minor },
        { tertiary, tertiary }
      };

      for (int k = 0; k < args.length; k++) {
        int mag = args[k][0];
        int vit = args[k][1];
        int expectedMp = expected[i][k];
        int mp = Stats.getMaxMp(mag, vit);
        assertEquals(expectedMp, mp);
      }
    }
  }

  @Test
  public void maxSpByCharacter() {
    assertEquals(1588, Stats.getMaxSp(ch));
  }

  @Test
  public void maxSpBySpeedAndStrength() {
    int[][] expected = new int[][] {
      {47,40,41,26,30,21,14},
      {867,746,779,521,579,443,322},
      {2362,2035,2129,1443,1588,1230,902},
      {4204,3624,3796,2589,2839,2212,1632},
      {6277,5411,5676,3886,4246,3322,2457}
    };
    int[] levels = new int[] { 1, 25, 50, 75, 100 };

    for (int i = 0; i < levels.length; i++) {
      int major = Stats.getAbility(levels[i], Stats.AbilityType.MAJOR);
      int minor = Stats.getAbility(levels[i], Stats.AbilityType.MINOR);
      int tertiary = Stats.getAbility(levels[i], Stats.AbilityType.TERTIARY);

      int[][] args = new int[][] {
        { major, minor },
        { major, tertiary },
        { minor, major },
        { minor, tertiary },
        { tertiary, major },
        { tertiary, minor },
        { tertiary, tertiary }
      };

      for (int k = 0; k < args.length; k++) {
        int spe = args[k][0];
        int str = args[k][1];
        int expectedSp = expected[i][k];
        int sp = Stats.getMaxSp(spe, str);
        assertEquals(expectedSp, sp);
      }
    }
  }

  @Test
  public void maxHpByMobile() {
    assertEquals(47733, Stats.getMaxHp(mob));
  }

  @Test
  public void mobileHPByLevelAndPower() {
    int[] powers = new int[] {
      1, 5, 10, 15, 20, 25, 30, 35,
      40, 45, 50, 55, 60, 65, 70, 75,
      80, 85, 90, 95, 100
    };
    int[] levels = new int[] {1, 50, 100};
    int[][] expected = new int[][] {
      {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
      {1026, 1898, 3997, 7006, 10816, 15360, 20590, 26470, 32969, 40064, 47733, 55960, 64727, 74021, 83830, 94141, 104946, 116234, 127997, 140227, 152915},
      {2402, 4457, 9398, 16481, 25451, 36150, 48464, 62307, 77609, 94312, 112369, 131736, 152377, 174259, 197353, 221631, 247069, 273645, 301340, 330132, 360006}
    };
    for (int i = 0; i < levels.length; i++) {
      int level = levels[i];
      for (int k = 0; k < powers.length; k++) {
        int power = powers[k];
        int expectedHP = expected[i][k];
        int actualHP = Stats.getMobileMaxHP(level, power);
        assertEquals(expectedHP, actualHP);
      }
    }
  }

  @Test
  public void acByMobile() {
    assertEquals(155, Stats.getAC(mob));
  }

  @Test
  public void mobileACByLevelAndPower() {
    int[] powers = new int[] {
      1, 5, 10, 15, 20, 25, 30, 35,
      40, 45, 50, 55, 60, 65, 70, 75,
      80, 85, 90, 95, 100
    };
    int[] levels = new int[] {1, 50, 100};
    int[][] expected = new int[][] {
      {5, 7, 10, 13, 16, 18, 21, 24, 27, 29, 32, 35, 38, 40, 43, 46, 49, 51, 54, 57, 60},
      {70, 77, 85, 94, 103, 111, 120, 129, 137, 146, 155, 163, 172, 180, 189, 198, 206, 215, 224, 232, 241},
      {189, 211, 239, 266, 294, 322, 349, 377, 405, 433, 460, 488, 516, 543, 571, 599, 627, 654, 682, 710, 738}
    };
    for (int i = 0; i < levels.length; i++) {
      int level = levels[i];
      for (int k = 0; k < powers.length; k++) {
        int power = powers[k];
        int expectedAC = expected[i][k];
        int actual = Stats.getMobileAC(level, power);
        assertEquals(expectedAC, actual);
      }
    }
  }

  @Test
  public void mobileChanceToHitByPower() {
    double[] chanceToHit = new double[] {
      0.40, 0.40, 0.42, 0.44, 0.45, 0.47, 0.50, 0.53, 0.54, 0.57, 0.59,
      0.62, 0.64, 0.67, 0.69, 0.71, 0.74, 0.77, 0.80, 0.82, 0.85
    };
    int[] powers = new int[] {
      1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75,
      80, 85, 90, 95, 100
    };
    for (int i = 0; i < powers.length; i++) {
      int power = powers[i];
      double expected = chanceToHit[i];
      double actual = Stats.getMobileChanceToHit(power);
      assertEquals(expected, actual, 0.1);
    }
  }

  @Test
  public void mobileAttackRollByLevelAndPower() {
    int[] levels = {1, 50, 100};
    int[] powers = {1, 50, 100};
    int[][] expected = new int[][] {
      {31, 47, 126},
      {528, 781, 2106},
      {942, 1395, 3759}
    };
    for (int i = 0; i < levels.length; i++) {
      int level = levels[i];
      for (int k = 0; k < powers.length; k++) {
        int power = powers[k];
        int actual = Stats.getMobileAttackRoll(level, power);
        int expect = expected[i][k];
        assertEquals(expect, actual);
      }
    }
  }

  @Test
  public void mobileAverageDamageByLevelAndPower() {
    int[] powers = new int[] {
      1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75,
      80, 85, 90, 95, 100
    };
    int[] levels = new int[] { 1, 50, 100 };
    int[][] damage = new int[][] {
      {1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 4},
      {63, 64, 66, 67, 69, 71, 74, 77, 80, 84, 88, 93, 99, 105, 113, 121, 132, 145, 161, 182, 210},
      {169, 171, 175, 179, 185, 191, 198, 205, 214, 224, 236, 249, 263, 281, 301, 325, 354, 389, 432, 488, 561}
    };

    for (int i = 0; i < levels.length; i++) {
      for (int k = 0; k < powers.length; k++) {
        int level = levels[i];
        int power = powers[k];
        int expected = damage[i][k];
        int actual = Stats.getMobileAverageDamage(level, power);
        assertEquals(expected, actual);
      }
    }
  }


  @Test
  public void armorBaseACByLevelAndSlot() {
    int[] levels = new int[] { 1, 50, 100 };
    String[] slots = new String[] {
      "head", "body", "hands", "legs", "waist", "feet", "off-hand"
    };
    Hashtable<String, LinkedList<Integer>> acs =
      new Hashtable<String, LinkedList<Integer>>();
    for (String slot : slots) {
      acs.put(slot, new LinkedList<Integer>());
    }

    acs.get("head").add(2); acs.get("head").add(28); acs.get("head").add(45);
    acs.get("body").add(5); acs.get("body").add(71); acs.get("body").add(114);
    acs.get("hands").add(1); acs.get("hands").add(14); acs.get("hands").add(22);
    acs.get("legs").add(3); acs.get("legs").add(42); acs.get("legs").add(68);
    acs.get("waist").add(1); acs.get("waist").add(14); acs.get("waist").add(22);
    acs.get("feet").add(2); acs.get("feet").add(28); acs.get("feet").add(45);
    acs.get("off-hand").add(3); acs.get("off-hand").add(42); acs.get("off-hand").add(68);

    for (int i = 0; i < levels.length; i++) {
      int level = levels[i];
      for (String slot : slots) {
        int expected = acs.get(slot).get(i);
        int actual = Stats.getArmorBaseAC(level, slot);
        assertEquals(expected, actual);
      }
    }
  }

  @Test
  public void weaponAttackRollByLevel() {
    int[] levels = new int[] { 1, 50, 100 };
    int[] expected = new int[] { 65, 325, 1005 };
    for (int i = 0; i < levels.length; i++) {
      assertEquals(expected[i], Stats.getWeaponAttackRoll(levels[i]));
    }
  }

  @Test
  public void weaponAverageDamageByLevel() {
    int[] levels = new int[] { 1, 50, 100 };
    int[] results = new int[] { 5, 2041, 4797 };
    for (int i = 0; i < levels.length; i++) {
      int expected = results[i];
      int actual = Stats.getWeaponAverageDamage(levels[i]);
      assertEquals(expected, actual);
    }
  }
}
