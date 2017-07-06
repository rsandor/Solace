package solace.cmd.play;

import solace.net.*;

/**
 * Displays a list of non-global cooldowns and the time remaning for each.
 * @author Ryan Sandor Richards
 */
public class Cooldown extends PlayStateCommand {
  public Cooldown(solace.game.Character ch) {
    super("cooldown", ch);
  }

  public void run(Connection c, String []params) {
    StringBuilder buffer = new StringBuilder();
    for (String cooldown : character.getCooldowns()) {
      int time = character.getCooldownDuration(cooldown);
      if (time < 1) continue;
      buffer.append(String.format("  [{C}%3ds{x}] {m}%s{x}\n\r", time, cooldown));
    }

    if (buffer.length() > 0) {
      character.sendln("Actions on cooldown:\n\r");
      character.sendln(buffer.toString());
    } else {
      character.sendln("You have no skills on cooldown.");
    }
  }
}
