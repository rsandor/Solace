package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * Displays a list of all buffs and debuffs currently affecting the player.
 * @author Ryan Sandor Richards
 */
public class Buffs extends PlayCommand {
  public Buffs(solace.game.Character ch) {
    super("buffs", ch);
  }

  public boolean run(Connection c, String []params) {
    StringBuilder buffs = new StringBuilder();
    StringBuilder debuffs = new StringBuilder();

    for (Buff buff : character.getBuffs()) {
      StringBuilder buffer = buff.isDebuff() ? debuffs : buffs;
      buffer.append(buff.isDebuff() ? "{R-{x" : "{g+{x");

      int time = buff.getTimeRemaining();
      StringBuilder timeRemaining = new StringBuilder();
      timeRemaining.append("{b({c");
      if (time > 3600) {
        timeRemaining.append(String.format(
          "%dh%dm", time / 3600, (time % 3600) / 60));
      } else if (time > 60) {
        timeRemaining.append(String.format("%dm%ds", time / 60, time % 60));
      } else {
        timeRemaining.append(String.format("%ds", time));
      }
      timeRemaining.append("{b){x");

      buffer.append(String.format(" %-16s {M%-18s{x %-50s\n\r",
        timeRemaining.toString(), buff.getName(), buff.getDescription()));
    }

    if (buffs.length() == 0 && debuffs.length() == 0) {
      character.sendln("You are affected by no buffs or debuffs.");
    } else {
      if (buffs.length() > 0) {
        character.sendln("Current Buffs:");
        character.sendln(buffs.toString());
      }
      if (debuffs.length() > 0) {
        character.sendln("Current Debuffs:");
        character.sendln(debuffs.toString());
      }
    }

    return true;
  }
}
