package solace.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Random number utility class.
 * @author Ryan Sandor Richards
 */
public class Rand {
  protected static final int NORMAL_ROLLS = 12;

  /**
   * @return A uniform random number.
   */
  public static double uniform() {
    return ThreadLocalRandom.current().nextDouble(0, 1);
  }

  /**
   * Returns a random integer from 1 to the given maximum (inclusive). This is
   * useful for performing rolls that have equal odds of landing on any given
   * number in the range (such as attack rolls).
   * @param  max Maximum for the roll.
   * @return A random number from 1 to the given maximum.
   */
  public static int uniformRoll(int max) {
    return ThreadLocalRandom.current().nextInt(1, max + 1);
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
   * @return A psuedo-normal random number.
   */
  public static int normalRoll(int mean) {
    double sum = 0.0;
    for (int i = 0; i < NORMAL_ROLLS; i++) {
      sum += uniform();
    }
    return (int)(2 * mean * sum / NORMAL_ROLLS);
  }
}
