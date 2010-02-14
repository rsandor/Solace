package solace.util;
import java.util.StringTokenizer;

/**
 * Contains various helper methods for handling input and output
 * @author Ryan Sandor Richards.
 */
public class Strings {
 	/**
   * Formats a string to a fixed width number of characters, useful for paragraphs.
   * @param s String to format.
   * @param w Maximum column width.
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
}