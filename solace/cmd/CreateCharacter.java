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
            solace.game.Character ch = new solace.game.Character(input);

            // TODO Need to flesh out the character creation once we have
            // classes and races. This will do for now to ensure each character
            // has basic stats.
            ch.setHp(20); ch.setMaxHp(20);
            ch.setMp(20); ch.setMaxMp(20);
            ch.setSp(20); ch.setMaxSp(20);
            ch.setStrength(8);
            ch.setVitality(8);
            ch.setMagic(8);
            ch.setSpeed(8);

            act.addCharacter(ch);
            act.save();
          }
          catch (IOException ioe) {
            Log.error(ioe.getMessage());
          }
          catch (NullPointerException npe) {
            Log.error("Null pointer exception: ");
            npe.printStackTrace();
          }
          finally {
            c.sendln("Character created!");
          }
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
