@name(necromancy)
@author(Ryan Sandor Richards)

# Skill: Necromancy (mag/vit)
The `necromancy` skill allows players to cast spells that maniuplate life and
death by way of wretched damage over time spells and control of undead pets.

The dark art; the craft unspoken. Little is known of the development of
necromancy, for it has been considered vile through all known history. Its
practitioners have long shied away from the lime light regardless of if their
intent be good or ill.

There are tales throughout the ages that speak of vast covens of necromancers
that band together to bring push the boundaries of knowledge within the school.
Nearly four centuries prior one such coven was said to have inscribed the
fruits of their labor into a book bound in flesh and written in blood... The
dreaded book was known as the Necronomicon.

## Passive Enhancements:
The following passive enhancements are granted by the necromancy skill at the
given skill levels:

* [Level 25] `Undead Savagery`
  Raised undead pets gain a 10-40% bonus to HP and each attack performed by a
  raised pets does an additional 10-50 potency damage (HP bonus an potency
  scale with skill level).
* [Level 50] `Dictator`
  Unlocks `advanced pet actions` for raised undead pets.
* [Level 75] `Siphoning`
  Damage due to debuffs on enemies heal raised pets by 25-50% of damage dealt
  (healing percentage scales with skill level).

## Cooldown Actions:
The following spells are granted by the necromancy skill at the given skill
levels:

* [level 5] `Raise Dead` (90s cooldown, 15s cast, 25% MP)
  By targeting a corpse, the player raises an undead being from the putrid
  remains. The following undead are raised from a corpse starting at the given
  skill level:

    [ 5] `Skeleton` - Low HP and AC with high offense.
    [25] `Zombie` - High HP and low AC with medium offense; vulnerable to fire
          and bludgeoning damage.
    [45] `Ghoul` - Medium HP and AC with high offense; performs two attacks per
         turn.
    [70] `Mummy` - High HP and AC; immune to slashing damage. Each attack
         by the mummy has a chance to inflict the `mummy rot` debuff for 10 to
         40s. The debuff deals 35-100 potency damage per round (damage and
         duration both scale with necromancy skill level).
    [95] `Lich` - High HP and AC; 50% resistance to physical damage. The lich
         performs 2 attacks per round and has a limited MP pool allowing it to
         cast: `flamestrike` (evocation), `armor` (alteration), and `heal`
         (restoration). Spells are cast with a skill level half that of the
         character.

  The raised undead becomes a pet that follows the caster and performs any
  special `pet actions` it is given. The mobile pet has the same level as the
  caster and that level scales as long as the pet is summoned. All undead
  pets absorb necrotic damage.

* [Level 20] `Darkness` (30s cooldown, 4s cast, 30% MP)
  Applies the `darkness` debuff to the target for 10 to 60s. The debuff deals
  20 to 50 potency necrotic damage to the target every 2 seconds and applies
  a 5-15% attack roll penalty (duration, damage potency, and attack penalty
  scale with skill level).
* [Level 40] `Lich Form` (180s cooldown, 6s cast, free)
  Applies the `lich` buff to the caster for 10s. The buff deals drains 7% of
  the caster's HP and restores 7% of their MP every second.
* [Level 60] `Drain Life` (40s cooldown, 6s cast, 45% MP)
  Applies the `drained` debuff to the target for 10 to 60s. The debuff deals
  50 to 100 potency necrotic damage to the target every 2 seconds, applies a
  10-20% AC penalty, and gives them vulnerability to fire, cold, and force
  damage (duration, damage potency, and AC penalty scale with skill level).
* [Level 95] `Sacrifice` (120s cooldown, 10s cast, 60% MP)
  The character orders its pet to consume necrotic energy and then explode
  dealing 1800 potency damage its current battle target and instantly dying.
