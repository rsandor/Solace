package solace.cmd.play;

import solace.util.*;
import solace.net.*;
import solace.cmd.*;
import java.util.*;

/**
 * Command that allows players to set custom prompt formats.
 * @author Ryan Sandor Richards
 */
public class Prompt extends PlayCommand {
  public Prompt(solace.game.Character ch) {
    super("prompt", ch);
  }

  public boolean run(Connection c, String []params) {
    if (params.length < 2) {
      c.sendln("Please enter a prompt format string to set.");
      return false;
    }

    if (params.length > 2) {
      c.sendln(
        "Setting first paramter as prompt (hint: try quotes '' or \"\")");
    }

    character.setPrompt(params[1]);
    return true;
  }
}
