# Master TODO

### In Progress

--------------------------------------------------------------------------------

### Backlog

**Task: Begin Scripting Documentation**
- [ ] Overview of scripting
- [ ] Creating Commands
- [ ] Creating Cooldowns

**Feature: Areas 2.0**
- Room scripting
  - Spawn points for mobiles and items
  - React to player enter/exit
  - Interactive features (levers, etc.)
- Mobile scripting
  - Wandering (with sensible built-in scripts)
  - Aggro Mobs  
- Area links: allow move command to work across areas
- Room lighting, light source equipment, etc.
- Banks (items and gold)
- Game world calendar + Weather
- Item weight & Carrying Capacity

**Feature: Books**
- [ ] Create book type items that can be read
  - [ ] Book: Liber Particularum Magicae (book of elemental magic)
        (reading this unlocks ultimate level 100 evocation skill!)

**Feature: Leveling System**
- Leveling Design
  - Mob kills? Power level adjustable?
  - Quests?
  - Skill leveling?

**Feature: Battle and Tactics System 2.0**
- Tactics System 1.0
- Stats 3.0
  - Floating point based
  - Fetch constants from a data file
    - Allow admins to reload stats on-the-fly
  - Better caching for faster lookup
  - Mild skill balancing and playtesting
- Interrupt casting (performing certain actions interrupts the spell, etc.)
- better attack roll potency scaling (currently too powerful)
- magic attacks currently based on weapon, should be different
  - [ ] Saving Throws unit testing
  - [ ] Spells and Spellcasting help articles
- better use of skill level to help "to hit" and "damage" rolls
- Flesh out player (apply a debuff?) and mobile death (generate a corpse and loot?)
- [ ] Full help article describing battle

**Feature: Quest System 1.0**
- Dungeon Instances 1.0
- Further design needed

**Feature: Single Player Instance Parties**
- Requires: dungeon instances and tactics

**Feature: Server 2.0**
- SSL Termination for secure client connection
- Connection proxy so the game can be fully recompiled and reloaded
- Data layer for storing game state (mongo?)

**Plugins (SPM)**
Allow anything that could inhabit the game directory to be coded as
a plugin. The idea would be that you'd have a command-line utility
called `spm` (Solace Plugin Manager) that works like so:
```
spm install rsandor/solace-race-darkelf
```
The utility would just fetch the repo from the following URL:
```
https://github.com/rsandor/solace-race-darkelf
```
And install it in the `game/plugins` directory.
