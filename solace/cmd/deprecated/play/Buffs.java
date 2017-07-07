package solace.cmd.deprecated.play;

import solace.cmd.play.AbstractPlayCommand;
import solace.game.*;

/**
 * Displays a list of all buffs and debuffs currently affecting the player.
 * @author Ryan Sandor Richards
 */
public class Buffs extends AbstractPlayCommand {
  private static final String[] aliases = {
    "affects"
  };

  public Buffs() {
    super("buffs", aliases);
  }

  public void run(Player player, String []params) {
    StringBuilder buffs = new StringBuilder();
    StringBuilder debuffs = new StringBuilder();

    for (Buff buff : player.getBuffs()) {
      StringBuilder buffer = buff.isDebuff() ? debuffs : buffs;
      buffer.append(buff.isDebuff() ? "{R}-{x}" : "{g}+{x}");

      int time = buff.getTimeRemaining();
      StringBuilder timeRemaining = new StringBuilder();
      timeRemaining.append("{b}({c}");
      if (time > 3600) {
        timeRemaining.append(String.format(
          "%dh%dm", time / 3600, (time % 3600) / 60));
      } else if (time > 60) {
        timeRemaining.append(String.format("%dm%ds", time / 60, time % 60));
      } else if (time >= 0) {
        timeRemaining.append(String.format("%ds", time));
      } else if (time <= Buff.TIME_REMAINING_INDEFINATE) {
        timeRemaining.append("special");
      }
      timeRemaining.append("{b}){x}");

      buffer.append(String.format(" %-16s {M}%-18s{x} %-50s\n\r",
        timeRemaining.toString(), buff.getName(), buff.getDescription()));
    }

    if (buffs.length() == 0 && debuffs.length() == 0) {
      player.sendln("You are affected by no buffs or debuffs.");
    } else {
      if (buffs.length() > 0) {
        player.sendln("Current Buffs:");
        player.sendln(buffs.toString());
      }
      if (debuffs.length() > 0) {
        player.sendln("Current Debuffs:");
        player.sendln(debuffs.toString());
      }
    }
  }
}
