package solace.util;

/**
 * Handles ANSI color formatting.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Color
{
  static char []escChar = { 27 };
  static String esc = new String( escChar );
  static String bright = esc +"[1m";
  static String black = esc + "[30m";
  static String red = esc + "[31m";
  static String green = esc + "[32m";
  static String yellow = esc + "[33m";
  static String blue = esc + "[34m";
  static String magenta = esc + "[35m";
  static String cyan = esc + "[36m";
  static String white = esc + "[37m";
  static String off = esc + "[0m";

  static String []text = {
    "\\{",
    "k", "K",
    "r", "R",
    "g", "G",
    "y", "Y",
    "b", "B",
    "m", "M",
    "c", "C",
    "w", "W",
    "x", "X"
    };

  static String []code = {
    "{",
    off+black, bright+black,
    off+red, bright+red,
    off+green, bright+green,
    off+yellow, bright+yellow,
    off+blue, bright+blue,
    off+magenta, bright+magenta,
    off+cyan, bright+cyan,
    off+white, bright+white,
    off, off
    };

  /**
   * Formats a string with in game color escapes to actual ANSI color escapes.
   * @param s String to parse and format.
   * @return A new copy of the string with the given parse information.
   */
  public static String format(String s)
  {
    String out = new String(s);
    for (int i = 0; i < code.length; i++)
      out = out.replaceAll("\\{"+text[i], code[i]);
    return out;
  }

  /**
   * Removes in game color escapes from a given string.
   * @param s String to strip of color escapes.
   * @return A new string with color escapes stripped from it.
   */
  public static String strip(String s)
  {
    String out = new String(s);
    for (int i = 0; i < code.length; i++)
      out = out.replaceAll("\\{"+text[i], "");
    return out;
  }
}
