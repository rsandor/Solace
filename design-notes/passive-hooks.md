# Passive and Buff Hooks
A running list of all passive and buff hooks we'd need to have in the game engine
to allow for the full scripting of all skills.

## Alteration
- Base HP, SP and MP recovery (Aspect of Selune)
- Modify AC (Armor)
- Modify Ability Score (Giant Strength)
- Modify Base Attack Potency (Giant Strength)
- Override item visibility (True Sight)
- Avoid Attacks (Sphere of Resistance)
- Modify Number of Attacks (Haste)

## Block
- Avoid Attacks (Deflect)
- Modify AC (Shield Mastery)
- Modify Damage Resistances (Defender)
- Reflect Attack Damage (Reflect)
- Prevent Attacks (`stunned` debuff from Bash/Pin)
- Prevent Movement (`stunned` debuff from Bash/Pin)
- Prevent "Spell-like" Cooldown Usage (`silenced` debuff from Throat Smash)

## Conjuration
- Set flags on the player (spc. advanced pet actions, Elemental Mastery)
- Pet AC (Greater Elementals)
- Pet Attack Rolls (Greater Elementals)
- Pet Number of Attacks (Greater Elementals)
- Damage / Time (`resonating` debuff, Resonance)
- Modify Damage Vulnerabilities (`resonating` debuff, Resonance)

## Evocation
- Damage / Time (`shocked` debuff, Shock)
- Reduce Number of Attacks (`slowed` debuff, Landslide)
- Reduce Attack Potency (`slowed` debuff, Landslide)
- Prevent Movement (`paralyzed` debuff, Lightning)
- Prevent Attacks (`stunned` debuff from Quake)
- Prevent Movement (`stunned` debuff from Quake)

## Heavy Armor
- Modify Damage Resistance (Heavy Mettle)
- Specific Armor Item AC Bonus (Flesh of Steel)
- Specific Armor Item AC Bonus (Rigid Stance)
- Specific Armor Item AC Bonus (Anchored)
- Resist Debuff (`stun` & `paralyze` Anchored)
- Healing HP / Battle Round (Regenerative Coating)
- Healing SP / Battle Round (Stalwart Stamina)
- Overall AC Bonus (Heavy Armor Mastery)
- Prevent Damage Before Applied / Absorb Damage (Steadfast)

## Light Armor
- Modify Damage Resistances (Though as Leather)
- Specific Armor AC Bonus (Second Hide)
- Modify Damage Resistances (Second Hide)
- Specific Armor AC Bonus (Inner Warmth)
- Modify Damage Resistances (Inner Warmth)
- Specific Armor AC Bonus (Fleet of Foot)
- Modify Ability Score (Fleet of Food)
- Increase Buff Duration (Longevity)
- Decrease Debuff Duration (Hasty Recovery)
- Overall AC Bonus (Light Armor Mastery)
- Re-roll on Missed Attack / Cooldown (Center of One)

## Necromancy
- Modify Pet Max HP (Undead Savagery)
- Modify Pet Base Attack Potency (Undead Savagery)
- Set flags on the player (spc. advanced pet actions, Dictator)
- Damage / Time Reaction (Siphoning)
- Damage / Time (`mummy rot` debuff, cast by Mummy pet from Raise Dead)
- Prevent Damage Before Applied / Absorb Damage (undead pets absorb necrotic)
- Damage / Time (`darkness` debuff, Darkness)
- Modify Attack Roll (`darknes` debuff, Darkness)
- Damage / Time (`drained` debuff, Drain Life)
- Modify AC (`drained` debuff, Drain Life)
- Modify Damage Vulnerabilities (`drained` debuff, Drain Life)

## One Handed
- Avoid Attack (Parry)
- Number of Attacks (Second Attack)
- Number of Attacks (Third Attack)
- Modify Base Attack Potency (Battle Trance)
- Modify Roll-to-hit (Battle Trance)

## Persuasion
- Modify sale price of items bought from stores (Bargain Hunter)
- Modify sale price of items sold to stores (Sales Executive)
- Base Attack Potency (Prey the Weak)
- Roll-to-hit Bonus (Cull the Low)
- Base Attack Potency (Cull the Low)
- Attack roll penalty (`enraged` debuff, Taunt)
- Attack damage penalty (`enraged` debuff, Taunt)
- Remove Aggression (`docile` debuff, Calm)
- Prevent Attacks (`distraught` debuff, Dishearten)
- Prevent Actions (`distraught` debuff, Dishearten)

## Ranged
- Modify Base Attack Potency (Keen Eye)
- Roll-to-hit Bonus (Sixth Sense)
- Number of Attacks (Second Attack)
- Apply Debuff on Attack (Crippling Shot)
- Number of Attacks (Third Attack)
- Pursuit of Player (Probably needs buff, from Snipe)
- Number of Attacks (`frenzied` buff, Frenzy)

## Ranging
- Base HP, SP and MP recovery (Nature's Child)
- Base Attack Potency (Weather Beaten)
- Attack roll-to-hit (Weather Beaten)
- Dual Wielding (This probably needs a whole engine feature via a flag, Dual Wield)
- Damage Resistance (Wrath of the Wood)
- Base Attack Bonus (Wrath of the Wood)
- Information / Time (`tracking`, Track)

## Restoration
- AC Bonus (`armored` buff, Shield)
- Modify Damage Resistances (`protected` buff, Protect)
- Healing / Time (`regenerating` buff, Regenerate)

## Stealth
- Trap detection (Detect Traps)
- Base Attack Potency (Precision Strike)
- Visibility State (`hidden` buff, Hide)

## Two Handed
- Modify roll-to-hit (Two-handed Finesse)
- Base Attack Potency (Blood Letter)
- Number of Attacks (Second Attack)
- Base Attack Potency (Fury of Keldar)
- Modify roll-to-hit (`blinded` debuff, Smash)
- Modify damage received (`prone` debuff, Sweep)

## Unarmed
- Number of Attacks (Second Attack)
- Number of Attacks (Third Attack)
- Number of Attacks (Fourth Attack)
- Stacked Buffs (`prana vayu` & `damana vayu`)

## Unarmored
- Avoid Attacks (Dodge)
- Specific Armor AC Bonus (Steel Skin)
- Specific Armor AC Bonus (Low Guard)
- Specific Armor AC Bonus (Fast Footwork)
- Resist Casting Interruption (Focused Mind)
- HP, MP, and SP Cost Discounts (Fluid Movement)
- Overall AC Bonus (Master of None)
- Casting Time Discount (Unhindered)
