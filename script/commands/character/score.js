'use strict';

/**
 * Score command, shows players their ability scores and statistics.
 * @author Ryan Sandor Richards
 */
Commands.add('score', function (player, params) {
  var buf = new StringBuilder();
  buf.append(Strings.RULE);
  var character = player.getCharacter();

  var title = format(
    "| {c}Name:{x} %s | {c}Level:{x} %d | {c}Race:{x} %s%s",
    character.getName(),
    character.getLevel(),
    character.getRace().getName(),
    character.isImmortal() ? " | {Y}[IMMORTAL]{x}" : ""
  );
  buf.append(
    title + Strings.spaces(
      80 - 1 - Color.strip(title).length()
    ) + "|\n\r"
  );

  buf.append(Strings.RULE);

  var acHpMpSp = format(
    "| {c}Armor Class:{x}    [{W}%4d{x}] | {c}HP:{x} {W}%d/%d{x} | {c}MP:{x} {W}%d/%d{x} | {c}SP:{x} {W}%d/%d{x} ",
    character.getAC(),
    character.getHp(), character.getMaxHp(),
    character.getMp(), character.getMaxMp(),
    character.getSp(), character.getMaxSp()
  );
  buf.append(acHpMpSp + Strings.spaces(80 -1 - Color.strip(acHpMpSp).length()) + "|\n\r");

  buf.append(Strings.RULE);

  var str = format(
    "| {c}Strength {x}({y}str{x}): [{W}%4d{x}] |   {c}Hit Mod:{x}       [{W}%4d{x}]   |   {c}Damage Mod:{x}  [{W}%4d{x}]",
    character.getStrength(),
    character.getHitMod(),
    character.getDamageMod()
  );
  buf.append(str + Strings.spaces(80 - 1 - Color.strip(str).length()) + "|\n\r");

  var vit = format(
    "| {c}Vitality {x}({y}vit{x}): [{W}%4d{x}] |   {c}Will Save:{x}     [{W}%4d{x}]   |   {c}Reflex Save:{x} [{W}%4d{x}]",
    character.getVitality(),
    character.getWillSave(),
    character.getReflexSave()
  );
  buf.append(vit + Strings.spaces(80 - 1 - Color.strip(vit).length()) + "|\n\r");

  var mag = format(
    "| {c}Magic    {x}({y}mag{x}): [{W}%4d{x}] |   {c}Resolve Save:{x}  [{W}%4d{x}]   |   {c}Vigor Save:{x}  [{W}%4d{x}]",
    character.getMagic(),
    character.getResolveSave(),
    character.getVigorSave()

  );
  buf.append(mag + Strings.spaces(80 - 1 - Color.strip(mag).length()) + "|\n\r");

  var spe = format(
    "| {c}Speed    {x}({y}spe{x}): [{W}%4d{x}] |   {c}Prudence Save:{x} [{W}%4d{x}]   |   {c}Guile Save:{x}  [{W}%4d{x}]",
    character.getSpeed(),
    character.getPrudenceSave(),
    character.getGuileSave()
  );
  buf.append(spe + Strings.spaces(80 - 1 - Color.strip(spe).length()) + "|\n\r");

  buf.append(Strings.RULE);

  character.sendln(buf.toString());
});
