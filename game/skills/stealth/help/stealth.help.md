@name(stealth)
@author(Ryan Sandor Richards)

# Skill: Stealth (spe/str)
The stealth skill provides a character with a slew of utility cooldowns and
attacks that use the sneaking, hiding, and the element of surprise.

## Passive Enhancements
The stealth skill provides the following passive at the given skill level:

* [Level 5] `Detect Traps`
  The character is capable of detecting traps with levels at or below their
  current stealth skill level. Traps much lower are easier to detect, while
  traps near the current level are harder.
* [Level 50] `Sneak`
  Movement no longer removes the `hidden` buff from the character.
* [Level 95] `Precision Strike`
  The `backstab` and `circle` cooldowns deal an additional 20% damage and
  regular attacks deal an additional 50 potency when the character is
  wielding a piercing weapon.

## Cooldown Actions
The stealth skill provides the following cooldown actions at the given skill
level:

* [Level 10] `Hide` (30s cooldown, 5% SP)
  The character draws into the shadows and gains the `hidden` buff. The buff
  remains as long as the player does not move, attack, or take any action
  that could be noticed by another player.
* [Level 15] `Pick Lock` (10s cooldown, 2% SP)
  Unlocks a single lock of a level at or below the character's stealth skill
  level.
* [Level 25] `Disarm Traps` (30s cooldown, 4% SP)
  The character attempts to disarm a trap on the given target. Chance of
  success is dependant on skill level; there is also a small chance that
  attempting to disarm the trap will set it off.
* [Level 35] `Backstab` (60s cooldown, 20% SP)
  Available only when the character has the `hidden` buff, this "opener"
  attack deals 250 to 1400 potency and applies the `weary` debuff to the
  target for 90s (potency scales with skill level). Mobiles with the `weary`
  debuff cannot be back-stabbed while it remains, further the debuff cannot
  be removed through the use of cooldowns.
* [Level 65] `Circle` (4s cooldown, 30% SP)
  The character circles around the target and performs an attack dealing
  300 to 700 potency to the flank or rear (potency scales with skill level).
