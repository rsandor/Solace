package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * Score command, shows players their ability scores and statistics.
 * @author Ryan Sandor Richards
 */
public class Score extends PlayCommand {
  public Score(solace.game.Character ch) {
    super("score", ch);
  }

  public boolean run(Connection c, String []params) {
    StringBuffer buf = new StringBuffer();

    buf.append(Strings.RULE);

    String title = String.format(
      "| {cName:{x %s | {cLevel:{x %d | {cHP:{x %d/%d | {cMP:{x %d/%d | {cSP:{x %d/%d ",
      character.getName(),
      character.getLevel(),
      character.getHp(), character.getMaxHp(),
      character.getMp(), character.getMaxMp(),
      character.getSp(), character.getMaxSp()
    );
    buf.append(
      title + Strings.spaces(80 - 1 - Color.strip(title).length()) + "|\n\r"
    );

    buf.append(Strings.RULE);

    String str = String.format(
      "| {cStrength {x({ystr{x): [{W%4d{x] | {cHit Mod:{x     [{W%4d{x]",
      character.getStrength(),
      character.getHitMod()
    );
    buf.append(str + Strings.spaces(80 - 1 - Color.strip(str).length()) + "|\n\r");

    String vit = String.format(
      "| {cVitality {x({yvit{x): [{W%4d{x] | {cDamage Mod:{x  [{W%4d{x]",
      character.getVitality(),
      character.getDamageMod()
    );
    buf.append(vit + Strings.spaces(80 - 1 - Color.strip(vit).length()) + "|\n\r");

    String mag = String.format(
      "| {cMagic    {x({ymag{x): [{W%4d{x] | {cArmor Class:{x [{W%4d{x]",
      character.getMagic(),
      character.getAC()
    );
    buf.append(mag + Strings.spaces(80 - 1 - Color.strip(mag).length()) + "|\n\r");

    String spe = String.format(
      "| {cSpeed    {x({yspe{x): [{W%4d{x] |",
      character.getSpeed()
    );
    buf.append(spe + Strings.spaces(80 - 1 - Color.strip(spe).length()) + "|\n\r");

    buf.append(Strings.RULE);

    character.sendln(buf.toString());

    return true;
  }
}
