package solace.cmd;

import solace.io.Messages;
import solace.net.*;
import solace.game.*;
import solace.util.*;

import java.io.*;


/**
 * State controller for creating a new character.
 * @author Ryan Sandor Richards
 */
class CreateCharacterController implements Controller {
  /**
   * State set for the <code>CreateCharacterController</code> state controller.
   */
  private enum State {

    CHOOSE_NAME("{c}Choose a name, or '{y}cancel{c}' to exit:{x} ") {
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

            ch.setMajorStat("strength");
            ch.setMinorStat("vitality");

            ch.setHp(ch.getMaxHp());
            ch.setMp(ch.getMaxMp());
            ch.setSp(ch.getMaxSp());

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

  private Connection connection;
  private State state;

  CreateCharacterController(Connection c) {
    connection = c;
    state = State.CHOOSE_NAME;
    c.sendln(Messages.get("NewCharacter"));
  }

  public String getPrompt() {
    return state.getPrompt();
  }

  /**
   * @see solace.cmd.Controller
   */
  public void parse(String input) {
    state = state.parse(connection, input);
    if (state == State.EXIT) {
      connection.setStateController( new MainMenuController(connection) );
    }
  }
}
