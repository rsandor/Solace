package solace.cmd.play;

import solace.net.*;

/**
 * Command that allows players to set custom prompt formats.
 * @author Ryan Sandor Richards
 */
public class Prompt extends PlayStateCommand {
  public Prompt(solace.game.Character ch) {
    super("prompt", ch);
  }

  public void run(Connection c, String []params) {
    if (params.length < 2) {
      c.sendln("Please enter a prompt format string to set.");
      return;
    }

    if (params.length > 2) {
      c.sendln(
        "Setting first paramter as prompt (hint: try quotes '' or \"\")");
    }

    character.setPrompt(params[1]);
  }
}
