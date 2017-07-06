package solace.cmd.deprecated.play;

import solace.net.*;

import java.text.DecimalFormat;

/**
 * Worth command, shows players how much of each game currency they are
 * currently holding.
 *
 * TODO Right now there is only gold, but we should introduce more currencies
 *      later (and this command will need to be updated).
 *
 * @author Ryan Sandor Richards
 */
public class Worth extends PlayStateCommand {
  public Worth(solace.game.Character ch) {
    super("worth", ch);
  }

  public void run(Connection c, String []params) {
    DecimalFormat dec = new DecimalFormat("#,###,###");
    c.wrapln(String.format(
      "You are currently carrying {y}%s{x} gold.",
      dec.format(character.getGold())
    ));
  }
}
