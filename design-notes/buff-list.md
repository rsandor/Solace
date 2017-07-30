# Buff List w/ Descriptions
List of all buffs and debuffs as described by the skill designs at this point.

## General Buff Attributes

- Level: Buffs are applied at a given level. For players this is generally the
  skill level for the cooldown that grants the buff. For mobiles this is generally
  based on the mobile level.
- Parametric: Some buffs can have additional parameters that dictate how they work.
  A good example `frenized` which takes a weapon type parameter and only doubles the
  number of attacks for that type.
- Stacking: Some buffs can be applied multiple times. Stacking buffs are generally
  used like a secondary resource for executing cooldowns, but may be more potent
  based on number of stacks.
- Mobile Only: These buffs can only be applied to mobiles as they have bearing only
  on engine mechanics that concern mobiles.

## Buffs
- `ghost armor`: grants the character the an AC bonus as if they were wearing an suit of
  heavy armor at 35% - 100% efficiency (scales with level)
- `strengthened`: grants a 10-25% bonus to the strength ability score and an additional
  10-50 potency damage for each attack.
- `truesight`:  While the buff is applied the character is capable of detecting and
   seeing objects, players, and mobiles that are under most forms of invisibility
   (a notable exception is the halfing's `vanished` racial buff).
- `impervious` (stacked): each stack of the buff will nullify a magical attack and
  then be shed.
- `hasted`: allows the character an extra attack in battle and provides a 10-25%
  bonus to AC (AC bonus and duration scale with skill level).
- `frenzied` (parametric): Doubles the number of attacks with weapons of a specific type.
- `tracking` (parametric): Provides information every 4 seconds on the direction of the
  given target relative to the player.
- `shielded`: Grants a 10-30% bonus to AC (scales with level).
- `protected`: Grants a 10-25% resistance to magical damage (scales with level).
- `regenerating`: Heals 1-8% of max HP every 2 seconds (scales with level).
- `hidden`: The player is not visible by normal means.
- `weary` (mobile only): Cannot be hit by the `backstab` skill.
- `prana vayu` (stacked, max 3): Buff used in conjunction with unarmed skill.
- `samana vayu` (stacked, max 3): Buff used in conjunction with unarmed skill


## Debuffs
- `blinded`: 10% to 35% penalty on attack rolls (scales with level).
- `darkness`: deals 20 to 50 potency necrotic damage to the target every 2 seconds and applies
   a 5-15% attack roll penalty (both scale with level)
- `distraught`: player can make no attacks and use no cooldowns / special attacks or powers.
- `docile` (mobile only): reduces mobile aggressiveness against players by 1-100% (scales with level).
- `drained`: deals 50 to 100 potency necrotic damage to the target every 2 seconds, applies a
  10-20% AC penalty, and gives them vulnerability to fire, cold, and force damage.
- `enraged`: applies a 10-30% penalty to attack rolls and a 5-20% penalty to damage dealt (scales with level).
- `mummy rot`: deals 35-100 potency damage per round (scales with level)
- `paralyzed`: prevents the player from fleeing battle or moving.
- `prone`: the player takes 10-25% more damage from attacks (scales with level).
- `resonating`: deals 20-60 potency force damage damage every 2 seconds and they also
  become vulnerable to the damage type element of any summoned pet at the time of casting.
- `silenced`: prevents the use of any cooldown or action that requires an activation
  time cost (e.g. spells).
- `stunned`: the player has a 50-80% chance of failing to attack for each attack in a
  round (scales with level).
- `shocked`: 20-30 `potency` damage / round (scales with level)
- `slowed`: reduces the number of attacks made by 1-3 (to a minimum of 1) and applies an
  AC penalty of 10-25% (both scale with level).
