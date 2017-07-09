package solace.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Handles ANSI color formatting.
 * @author Ryan Sandor Richards
 */
public class Color {
  private static char []escChar = { 27 };
  private static String esc = new String( escChar );
  private static String bright = esc +"[1m";
  private static String black = esc + "[30m";
  private static String red = esc + "[31m";
  private static String green = esc + "[32m";
  private static String yellow = esc + "[33m";
  private static String blue = esc + "[34m";
  private static String magenta = esc + "[35m";
  private static String cyan = esc + "[36m";
  private static String white = esc + "[37m";
  private static String off = esc + "[0m";

  /**
   * Handles color replacement for a single color.
   */
  private static class ColorReplacer {
    private Pattern pattern;
    private String escapeCode;

    ColorReplacer(String c, String e) {
      pattern = Pattern.compile(String.format("([^\\\\]|^)\\{%s\\}", c));
      escapeCode = e;
    }

    String replaceAll(String s) {
      return pattern.matcher(s).replaceAll("$1"+escapeCode);
    }

    String stripAll(String s) {
      return pattern.matcher(s).replaceAll("$1");
    }
  }

  /**
   * Color escape code replacer list.
   */
  private static final Collection<ColorReplacer> replacers = Arrays.asList(
    new ColorReplacer("k", off + black),
    new ColorReplacer("K", bright + black),
    new ColorReplacer("r", off + red),
    new ColorReplacer("R",bright+red),
    new ColorReplacer("g", off + green),
    new ColorReplacer("G", bright + green),
    new ColorReplacer("y", off + yellow),
    new ColorReplacer("Y", bright + yellow),
    new ColorReplacer("b", off + blue),
    new ColorReplacer("B", bright + blue),
    new ColorReplacer("m", off + magenta),
    new ColorReplacer("M", bright + magenta),
    new ColorReplacer("c", off + cyan),
    new ColorReplacer("C", bright + cyan),
    new ColorReplacer("w", off + white),
    new ColorReplacer("W", bright + white),
    new ColorReplacer("x", off),
    new ColorReplacer("X", off)
  );

  /**
   * Formats a string with in game color escapes to actual ANSI color escapes.
   * @param s String to parse and format.
   * @return A new copy of the string with the given parse information.
   */
  public static String format(String s) {
    for (ColorReplacer r : replacers) {
      s = r.replaceAll(s);
    }
    return s.replaceAll("[\\\\]\\{", "{");
  }

  /**
   * Removes in game color escapes from a given string.
   * @param s String to strip of color escapes.
   * @return A new string with color escapes stripped from it.
   */
  public static String strip(String s) {
    for (ColorReplacer r : replacers) {
      s = r.stripAll(s);
    }
    return s.replaceAll("[\\\\]\\{", "{");
  }
}
