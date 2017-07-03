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
      "| {cName:{x %s | {cLevel:{x %d | {cRace:{x %s%s",
      character.getName(),
      character.getLevel(),
      character.getRace().getName(),
      character.isImmortal() ? " | {Y[IMMORTAL]{x" : ""
    );
    buf.append(
      title + Strings.spaces(80 - 1 - Color.strip(title).length()) + "|\n\r"
    );

    buf.append(Strings.RULE);

    String acHpMpSp = String.format(
      "| {cArmor Class:{x    [{W%4d{x] | {cHP:{x {W%d/%d{x | {cMP:{x {W%d/%d{x | {cSP:{x {W%d/%d{x ",
      character.getAC(),
      character.getHp(), character.getMaxHp(),
      character.getMp(), character.getMaxMp(),
      character.getSp(), character.getMaxSp()
    );
    buf.append(acHpMpSp + Strings.spaces(80 -1 - Color.strip(acHpMpSp).length()) + "|\n\r");

    buf.append(Strings.RULE);

    String str = String.format(
      "| {cStrength {x({ystr{x): [{W%4d{x] |   {cHit Mod:{x       [{W%4d{x]   |   {cDamage Mod:{x  [{W%4d{x]",
      character.getStrength(),
      character.getHitMod(),
      character.getDamageMod()
    );
    buf.append(str + Strings.spaces(80 - 1 - Color.strip(str).length()) + "|\n\r");

    String vit = String.format(
      "| {cVitality {x({yvit{x): [{W%4d{x] |   {cWill Save:{x     [{W%4d{x]   |   {cReflex Save:{x [{W%4d{x]",
      character.getVitality(),
      character.getWillSave(),
      character.getReflexSave()
    );
    buf.append(vit + Strings.spaces(80 - 1 - Color.strip(vit).length()) + "|\n\r");

    String mag = String.format(
      "| {cMagic    {x({ymag{x): [{W%4d{x] |   {cResolve Save:{x  [{W%4d{x]   |   {cVigor Save:{x  [{W%4d{x]",
      character.getMagic(),
      character.getResolveSave(),
      character.getVigorSave()

    );
    buf.append(mag + Strings.spaces(80 - 1 - Color.strip(mag).length()) + "|\n\r");

    String spe = String.format(
      "| {cSpeed    {x({yspe{x): [{W%4d{x] |   {cPrudence Save:{x [{W%4d{x]   |   {cGuile Save:{x  [{W%4d{x]",
      character.getSpeed(),
      character.getPrudenceSave(),
      character.getGuileSave()
    );
    buf.append(spe + Strings.spaces(80 - 1 - Color.strip(spe).length()) + "|\n\r");

    buf.append(Strings.RULE);

    character.sendln(buf.toString());

    return true;
  }
}
