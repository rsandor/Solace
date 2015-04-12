package solace.cmd;

import solace.net.*;
import solace.game.*;
import solace.util.*;

import java.io.*;


/**
 * State controller for creating a new character.
 * @author Ryan Sandor Richards
 */
class CreateCharacter implements StateController {
  /**
   * State set for the <code>CreateCharacter</code> state controller.
   */
  private enum State {

    CHOOSE_NAME("{cChoose a name, or '{ycancel{c' to exit:{x ") {
      public State parse(Connection c, String input) {
        if (input.trim().equals("cancel")) {
          c.sendln("Character creation cancelled.");
          return EXIT;
        }
        else if (!input.matches("^[\\w]+$")) {
          c.sendln("Inavalid name, please only use letters and a single word.");
          return CHOOSE_NAME;
        }
        else {
          try {
            Account act = c.getAccount();
            act.addCharacter(new solace.game.Character(input));
            act.save();
          }
          catch (IOException ioe) {

          }
          c.sendln("Character created!");
          return EXIT;
        }
      }
    },

    EXIT("");

    String prompt;

    /**
     * Creates a new state.
     * @param p Prompt for the state.
     */
    State(String p) {
      prompt = p;
    }

    /**
     * @return The prompt for the state.
     */
    public String getPrompt() {
      return prompt;
    }

    /**
     * Parses a command.
     * @param c Connection that submitted the command.
     * @param input Command input to parse.
     * @return The next state for the connection.
     */
    public State parse(Connection c, String input) {
      return CHOOSE_NAME;
    }
  }

  Connection connection;
  State state;

  public CreateCharacter(Connection c) {
    init(c);
    state = state.CHOOSE_NAME;
    c.sendln(Message.get("NewCharacter"));
    c.setPrompt(state.getPrompt());
  }

  public void init(Connection c) {
    connection = c;
  }

  /**
   * @see solace.cmd.StateController.force();
   */
  public void force(String command) {
    parse(command);
  }

  /**
   * @see solace.cmd.StateController.parse();
   */
  public void parse(String input) {
    state = state.parse(connection, input);
    if (state == State.EXIT)
      connection.setStateController( new MainMenu(connection) );
    else
      connection.setPrompt(state.getPrompt());
  }
}
