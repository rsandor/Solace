@name(evocation)
@author(Ryan Sandor Richards)

# Skill: Evocation (mag/spe)
The evocation skill allows a character to cast spells that manipulate the four
elements: ice, fire, lightning, and earth. Primarily an offense oriented skill
the spells gained through mastery of the evocation skill can, when deftly used,
bring even the mightiest foes to their knees.

According to contemporary scholars an ancient grimoire by the name of "Liber
Particularum Magicae" exists on the subject of evocation mastery. It is rumored
that its pages contain deep unknown truths of the elements along with a plethora
of strategies and synergies crafted by the greatest arch-mages of yore. Finding
a complete copy of the tome, however, may prove no small feat.

## Cooldown Actions:
The following spell cooldowns are granted by the evocation skill at the given
skill levels:

* [Level 5] `Icespike` (GCD, instant, free, save: prudence, )
  An icicle is formed near the casters hand and thrown directly at the target
  dealing potency 70 `cold` damage. Heals 5-10% of the caster's MP.
* [Level 15] `Flamestrike` (GCD, 2s cast, 30% MP, save: reflex)
  Flames erupt from the caster's hands and envelop the target dealing `fire`
  damage with potency 400.
* [Level 25] `Shock` (10s cooldown, 3s cast, 5% MP, save: will)
  Electricity envelops the target applying a 30 second `shocked` debuff that
  deals 10-20 potency `lightning` damage to the target each round.
* [Level 35] `Landslide` (90s cooldown, 6s cast, special cost, save: vigor)
  Using the remainder of her MP, the caster causes a wave of earth to burst forth
  and sweep across the target. The spell deals 0 to 1000 potency bludgeoning
  damage (proportional to the amount of MP used) and also applies the `slowed`
  debuff for 4-12 seconds (duration scales with skill level).
* [Level 55] `Avalanche` (GCD, instant, free, save: prudence)
  An avalanche appears as if from nowhere toppling the target and dealing `cold`
  damage with potency 70. Heals 5-10% of caster MP. {G}Combo:{x} Icespike;
  potency increased to 150 and MP healing increased to 10-15%.
* [Level 65] Fireball (GCD, 3s cast, 50% MP, save: reflex)
  A massive fireball appears immolating the target and dealing `fire` damage
  with potency 400. {G}Combo:{x} Flamestrike, increases potency to 800.
* [Level 75] `Lightning` (3s cooldown, 4s cast, 10% MP, save: will)
  A lightning bolt shoots from the casters hand striking the target and dealing
  `lightning` damage with potency 200. If the target is affected by the `shock`
  debuff they are also then afflicted with the `paralyzed` debuff for 6s.
* [Level 85] `Quake` (180s cooldown, 8s cast, special cost, save: vigor)
  Using the remainder of her MP, the caster focuses aether to cause a great
  swelling and upheaval from the earth. Up to two targets chosen by the caster
  are caught in the quake and dealt `bludgeoning` damage with potency ranging
  from 0 to 1000 (proportional to the amount of MP used). {G}Combo:{x} landslide
  on cooldown; potency is increased to 0-2000, up to three targets may be selected,
  and each are afflicted by the `stunned` debuff for 4 seconds.
