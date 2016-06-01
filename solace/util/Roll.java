package solace.util;

import java.util.Random;

/**
 * Random number utility class.
 * @author Ryan Sandor Richards
 */
public class Roll {
  /**
   * Number of rolls to perform to generate a normal-ish distribution.
   */
  static final int NORMAL_ROLLS = 12;
  static final Random random = new Random();

  /**
   * Determines a random index given an array length.
   * @param  length Lenght of the array.
   * @return A random integer index for that array.
   */
  public static int index(int length) {
    return random.nextInt(length);
  }

  /**
   * @return A uniform random number.
   */
  public static double uniform() {
    return random.nextDouble();
  }

  /**
   * Returns a random integer from 1 to the given maximum (inclusive). This is
   * useful for performing rolls that have equal odds of landing on any given
   * number in the range (such as attack rolls).
   * @param  max Maximum for the roll.
   * @return A random number from 1 to the given maximum.
   */
  public static int uniform(int max) {
    return random.nextInt(max + 1) + 1;
  }

  /**
   * Returns a random integer that follows a noram distributed bell curve
   * with the given mean. Useful for calculating rolls that tend towards the
   * given average (such as damage rolls).
   *
   * Note that this does not actually use the statistical definition of a
   * normal distribution, but simulates it by generating and summing random
   * uniforms and scaling them appropriately to fit the given mean.
   *
   * @param mean Mean value for the distribution.
   * @return A psuedo-normal psuedo-random number.
   */
  public static int normal(int mean) {
    double sum = 0.0;
    for (int i = 0; i < NORMAL_ROLLS; i++) {
      sum += uniform();
    }
    return (int)(2 * mean * sum / NORMAL_ROLLS);
  }
}
