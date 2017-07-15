@name(two-handed)
@author(Ryan Sandor Richards)

# Skill: Two-handed (str/vit)
The two-handed skill allows the character to effectively battle using weapons
that require both hands to wield.

## Weapon Proficiencies
Characters with the two-handed skill become proficient in the use of the
following weapon types at the given skill level:

* [Level  1] - `quarterstaff`, `longspear`, `greatclub`
* [Level 20] - `greatsword`, `greataxe`, `lance`
* [Level 40] - `heavy-flail`, `halberd`, `guisarme`
* [Level 60] - `glaive`, `spiked-chain`, `scythe`

## Passive Enhancements:
The two-handed skill provides the following passive enhancements when a
character is wielding a two-handed weapon at the given skill level:

* [Level 20] `Weapon Focus`
  Attacks with two-handed weapons gain a 15-30% bonus to hit (depending on
  two-handed skill level).
* [Level 40] `Blood Letter`
  Attacks with two-handed weapons deal an additional 50 potency damage.
* [Level 70] `Second Attack`
  Make two attacks per round in battle.
* [Level 90] `Fury of Keldar`
  Attacks with two-handed weapons against debuffed opponents deal an additional
  50 potency damage.

## Cooldown Actions:
The two-handed skill provides the following cooldown actions when a character
is wielding a two-handed weapon type at the given skill level:

* [Level 10] `Smash` (GCD, 5% SP)
  A brutal attack with the two-handed weapon's hilt that deals 175 potency
  damage to an opponent. On hit there is a 10-30% chance (depending on
  two-handed skill level)  to apply the `blinded` debuff to the opponent for 4s.
* [Level 30] `Whirlwind` (GCD, 10% SP)
  By converting linear into rotational energy the two-handed weapon wielder
  unleashes a devastating series of attacks on an opponent dealing damage with
  potency 225. If the opponent is currently `blinded` then the potency increases
  to 350.
* [Level 65] `Sweep` (10s, 15% SP)
  A quick and deadly attack that attempts to sweep the enemy off their feet. The
  attack deals damage with potency 300 and applies the `prone` debuff for 4s.
* [Level 100] `Decapitate` (10s, 25% SP)
  Via a mighty swing from a two-handed weapon this attack deals damage with
  500 potency. If the enemy is currently `prone` then this attack has 5% chance
  of instantly killing them (note: some mobiles are immune to being instantly
  killed). If the opponent is not instantly killed it deals an additiona 100
  potency damage instead (for a total of 600 potency).
