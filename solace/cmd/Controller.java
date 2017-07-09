package solace.cmd;
import solace.net.Connection;

/**
 * Controllers are used to control game flow via user input.
 * @author Ryan Sandor Richards (Gaius)
 */
public interface Controller {
  /**
   * Parses user input and attempts to execute an appropriate command (if any).
   * @param input Input to parse.
   */
  void parse(String input);

  /**
   * @return The prompt the connection should send to the client.
   */
  String getPrompt();
}
