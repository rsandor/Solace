package solace.util;

import java.util.*;
import java.util.regex.*;

/**
 * Utility class for performing dice rolls.
 * @author Ryan Sandor Richards
 */
public class Dice {
  /**
   * Pattern for matching dice strings such as "1d6 + 4"
   */
  static Pattern dicePatternWithMod = Pattern.compile(
    "^\\s*([0-9]+)\\s*d\\s*([0-9]+)\\s*([+-])\\s*([0-9]+)\\s*$"
  );

  /**
   * Pattern for matching dice strings such as "1d8"
   * @return [description]
   */
  static Pattern dicePattern = Pattern.compile(
    "^\\s*([0-9]+)\\s*d\\s*([0-9]+)\\s*$"
  );

  /**
   * Random number generator.
   */
  static Random rand = new Random();

  /**
   * Rolls the given dice.
   * @param  diceString A string denoting the number of dice to roll and any
   *   modifiers.
   * @return The result from rolling the dice.
   * @throws DiceParseException If the given dice string is invalid.
   */
  public static int roll(String diceString)
    throws DiceParseException
  {
    try {
      Matcher m = dicePatternWithMod.matcher(diceString);
      Matcher m2 = dicePattern.matcher(diceString);

      int number = 0;
      int sides = 0;
      int mod = 0;

      if (m.matches()) {
        number = Integer.parseInt(m.group(1));
        sides = Integer.parseInt(m.group(2));
        mod = (m.group(3).equals("+") ? 1 : -1) *
          Integer.parseInt(m.group(4));
      }
      else if (m2.matches()){
        number = Integer.parseInt(m2.group(1));
        sides = Integer.parseInt(m2.group(2));
      }
      else {
        String msg = String.format("Could not parse dice string: %s", diceString);
        Log.error(msg);
        throw new DiceParseException(msg);
      }

      int total = 0;
      for (int i = 0; i < number; i++) {
        total += rand.nextInt(sides) + 1;
      }

      total += mod;

      Log.trace(String.format(
        "Dice roll %s yielded a total of %d.",
        diceString,
        total
      ));

      return total;
    }
    catch (NumberFormatException nfe) {
      // This should never happen if our dice pattern is defined correctly
      Log.error("Dice.roll: group failed to parse as integer.");
    }
    return 0;
  }
}
