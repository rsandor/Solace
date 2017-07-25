@name(ranged)
@author(Ryan Sandor Richards)

# Skill: Ranged (spe/str)
The ranged skill allows the character to effectively battle using a variety of
ranged weapons. Ranged weapons are either depletable (e.g. darts) or require
ammunition that is depletable (e.g. arrows for bows). The common types of
ammunition are: `arrows` (bows), `bolts` (crossbows), and `bullets` (slings).

## Weapon Proficiencies
Characters with the ranged skill become proficient in the use of the following
weapon types:

  `dart`, `hand-crossbow`, `crossbow`, `shortbow`, `longbow`, `shuriken`, `sling`,
  `throwing-dagger`

## Passive Enhancements:
The ranged skill provides the following passive enhancements when a character
wields a ranged weapon at the given skill level:

* [Level 15] `Keen Eye`
  Each attack with a ranged weapon has a 5-50% chance of dealing an additional
  100 potency damage (chance scales with skill level).
* [Level 35] `Sixth Sense`
  Each attack with a ranged weapon has a 5-25% bonus on rolls to hit (chance
  scales with skill level).
* [Level 55] `Second Attack`
  Make two attacks per round in battle.
* [Level 75] `Crippling Shot`
  Each attack with a ranged weapon has a 5-15% chance (depending on skill
  level) of inflicting one either the `paralyzed`, `blinded`, or `stunned`
  debuffs for 5s.
* [Level 100] `Third Attack`
  Make three attacks per round in battle.

## Cooldown Actions:
The ranged skill provides the following cooldown actions when a character
wields a ranged weapon at the given skill level:

* [Level 25] `Barrage` (GCD, 15% SP)
  The player sends a barrage of ranged weapon attack against their foe dealing
  potency 200 damage and consuming 3 pieces of ammunition.
* [Level 45] `Snipe` (Cooldown 10s, usage time 2s, 25% SP)
  The player chooses a target within two rooms from their current position and
  performs a long range attack with a normal attack roll and potency 350 damage.
  The target then becomes aggressive towards the player and will attempt to
  pursue and attack them (even across area boundaries).
* [Level 85] `Frenzy` (Cooldown 90s, 50% SP)
  Applies the `frenzied` buff to the player for 8s, doubling the number of
  ranged attacks made each round of battle.
