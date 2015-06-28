package solace.util;
import java.util.StringTokenizer;

/**
 * Contains various helper methods for handling input and output
 * @author Ryan Sandor Richards.
 */
public class Strings {
  public static final String RULE =
    "o------------------------------------------------------------------------------o\n\r";

  /**
   * Formats a string to a fixed width number of characters, useful for
   * paragraphs.
   * @param s String to format.
   * @param w Maximum column width.
   * @return The formatted string.
   */
  public static String toFixedWidth(String s, int w) {
    StringTokenizer tokenizer = new StringTokenizer(s);
    StringBuffer buf = new StringBuffer();
    int col = 0;

    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (col + 1 + token.length() > w) {
        buf.append("\n" + token);
        col = token.length();
        continue;
      }
      else if (col > 0) {
        buf.append(" ");
        col++;
      }
      buf.append(token);
      col += token.length();
    }

    return buf.toString();
  }

  /**
   * Formats a string to a fixed width of 80 characters.
   * @param s String to format.
   * @return The formatted string.
   */
  public static String toFixedWidth(String s) {
    return toFixedWidth(s, 80);
  }

  /**
   * Returns a string containing the given number of space characters.
   * @param n Number of spaces for the string.
   * @return A string with exactly the given number of spaces.
   */
  public static String spaces(int n) {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < n; i++) {
      b.append(' ');
    }
    return b.toString();
  }
}
