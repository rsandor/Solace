package solace.util;
import java.util.*;

/**
 * Utility class for parsing client input commands.
 * @author Ryan Sandor Richards
 */
public class CommandParser {
  protected static enum ParseState {
    NORMAL,
    SINGLE_QUOTE,
    DOUBLE_QUOTE
  };

  /**
   * Parses a command into an arguments array and handles quoting.
   * @param command The command to parse.
   * @return An array of command arguments.
   */
  public static String[] parse(String command) {
    LinkedList<String> params = new LinkedList<String>();
    StringBuffer quoted = null;
    ParseState state = ParseState.NORMAL;

    // Tokenize and parse the command looking for runs of quoted params...
    for (String token : command.split("\\s+")) {
      switch (state) {
        case NORMAL:
          if (
            (token.startsWith("'") && token.endsWith("'")) ||
            (token.startsWith("\"") && token.endsWith("\""))
          ) {
            params.add(token.substring(1, token.length() - 1));
          } else if (token.startsWith("\"") || token.startsWith("'")) {
            quoted = new StringBuffer();
            quoted.append(token.substring(1));
            quoted.append(" ");
            if (token.startsWith("'")) {
              state = ParseState.SINGLE_QUOTE;
            } else {
              state = ParseState.DOUBLE_QUOTE;
            }
          } else {
            params.add(token);
          }
          break;
        case SINGLE_QUOTE:
          if (token.endsWith("'")) {
            quoted.append(token.substring(0, token.length() - 1));
            params.add(quoted.toString());
            quoted = null;
            state = ParseState.NORMAL;
          } else {
            quoted.append(token);
            quoted.append(" ");
          }
          break;
        case DOUBLE_QUOTE:
          if (token.endsWith("\"")) {
            quoted.append(token.substring(0, token.length() - 1));
            params.add(quoted.toString());
            quoted = null;
            state = ParseState.NORMAL;
          } else {
            quoted.append(token);
            quoted.append(" ");
          }
          break;
      }
    }

    // Implicitly close quotes at the end of the string...
    if (quoted != null && quoted.length() > 0) {
      String quotedString = quoted.toString();
      params.add(quotedString.substring(0, quotedString.length() - 1));
    }

    return params.toArray(new String[params.size()]);
  }
}
