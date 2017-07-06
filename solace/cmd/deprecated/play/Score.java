package solace.cmd.deprecated.play;

import solace.net.*;
import solace.util.*;

/**
 * Score command, shows players their ability scores and statistics.
 * @author Ryan Sandor Richards
 */
public class Score extends PlayStateCommand {
  public Score(solace.game.Character ch) {
    super("score", ch);
  }

  public void run(Connection c, String []params) {
    StringBuffer buf = new StringBuffer();

    buf.append(Strings.RULE);

    String title = String.format(
      "| {c}Name:{x} %s | {c}Level:{x} %d | {c}Race:{x} %s%s",
      character.getName(),
      character.getLevel(),
      character.getRace().getName(),
      character.isImmortal() ? " | {Y}[IMMORTAL]{x}" : ""
    );
    buf.append(
      title + Strings.spaces(
        80 - 1 - Color.strip(title).length() + (character.isImmortal() ? 3 : 0)
      ) + "|\n\r"
    );

    buf.append(Strings.RULE);

    String acHpMpSp = String.format(
      "| {c}Armor Class:{x}    [{W}%4d{x}] | {c}HP:{x} {W}%d/%d{x} | {c}MP:{x} {W}%d/%d{x} | {c}SP:{x} {W}%d/%d{x} ",
      character.getAC(),
      character.getHp(), character.getMaxHp(),
      character.getMp(), character.getMaxMp(),
      character.getSp(), character.getMaxSp()
    );
    buf.append(acHpMpSp + Strings.spaces(80 -1 - Color.strip(acHpMpSp).length()) + "|\n\r");

    buf.append(Strings.RULE);

    String str = String.format(
      "| {c}Strength {x}({y}str{x}): [{W}%4d{x}] |   {c}Hit Mod:{x}       [{W}%4d{x}]   |   {c}Damage Mod:{x}  [{W}%4d{x}]",
      character.getStrength(),
      character.getHitMod(),
      character.getDamageMod()
    );
    buf.append(str + Strings.spaces(80 - 1 - Color.strip(str).length()) + "|\n\r");

    String vit = String.format(
      "| {c}Vitality {x}({y}vit{x}): [{W}%4d{x}] |   {c}Will Save:{x}     [{W}%4d{x}]   |   {c}Reflex Save:{x} [{W}%4d{x}]",
      character.getVitality(),
      character.getWillSave(),
      character.getReflexSave()
    );
    buf.append(vit + Strings.spaces(80 - 1 - Color.strip(vit).length()) + "|\n\r");

    String mag = String.format(
      "| {c}Magic    {x}({y}mag{x}): [{W}%4d{x}] |   {c}Resolve Save:{x}  [{W}%4d{x}]   |   {c}Vigor Save:{x}  [{W}%4d{x}]",
      character.getMagic(),
      character.getResolveSave(),
      character.getVigorSave()

    );
    buf.append(mag + Strings.spaces(80 - 1 - Color.strip(mag).length()) + "|\n\r");

    String spe = String.format(
      "| {c}Speed    {x}({y}spe{x}): [{W}%4d{x}] |   {c}Prudence Save:{x} [{W}%4d{x}]   |   {c}Guile Save:{x}  [{W}%4d{x}]",
      character.getSpeed(),
      character.getPrudenceSave(),
      character.getGuileSave()
    );
    buf.append(spe + Strings.spaces(80 - 1 - Color.strip(spe).length()) + "|\n\r");

    buf.append(Strings.RULE);

    character.sendln(buf.toString());
  }
}
