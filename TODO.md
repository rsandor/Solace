# Master TODO

### In Progress

**Feature: Skills 2.0**
  - [x] Weapon Proficiencies
    - [x] Common extension `.weapon.json`
    - [x] Associate weapon proficiencies with skills
  - [ ] Damage types
    - [ ] Common extension: `.damage.json`
    - [ ] Weapon damage types (associated with types of weapons)
      - [x] Associate damage types for weapon proficiencies
      - [ ] Check for invalid types
    - [ ] Armor resistance, vulnerability, immunity for types
    - [ ] Player (Mobile/Character) innate damage resistances
  - [ ] Design remaining skills
    - [x] Unarmed
    - [x] Two-handed
    - [x] Ranged
    - [x] Unarmored
    - [x] Light-armor
    - [x] Heavy-armor
    - [x] Block
    - [ ] Restoration
    - [ ] Alteration
    - [ ] Necromancy
    - [ ] Conjuration
    - [ ] Stealth
    - [ ] Persuasion
    - [ ] Ranging
  - [ ] Scriptable Buffs and Passives
    - Need to work out a way to handle buffs and passives as scripts
    - Seems to require a lot of hooks into the engine to know when to apply them
    - How do we handle things like additional attacks?
    - Resource costs?
    - Ability score buffs / passives?
  - [ ] Incorporate ability scores into game math for skills
  - [ ] Incorporate skill level into game math for skill cooldowns

--------------------------------------------------------------------------------

### Bugs

- [ ] **Bug:** Shutdown command sometimes hangs, investigate
      Seems to hang when there are multiple logged in players
      It catches right after it says "Stopping account writer"

--------------------------------------------------------------------------------

### Backlog

**Feature: Character Creation 2.0**
- [ ] Account files should be saved to JSON
- [ ] Character Gender
- [ ] New Interactive Character Creator
 - [ ] Provide access to help command from within creator

**Task: Adopt AssetManager Pattern**
- [ ] Emotes
- [ ] Buffs
- [ ] Configs
- [ ] HelpSystem
- [ ] Messages
- [ ] Races
- [ ] Skills
- [ ] Weapon Types

**Task: Begin Scripting Documentation**
- [ ] Overview of scripting
- [ ] Creating Commands
- [ ] Creating Cooldowns

**Feature: Scriptable Communication Channels**
- [ ] Communication manager service
  - Design an interface for scripts to register chat channels / commands
- [ ] Implement built-in channels:
  - [ ] `ooc` - Global out of character chat
  - [ ] `shout` - Area specific channel
  - [ ] `newbie` - New player QA channel
  - [ ] `ask/answer` - General QA channel
  - [ ] `info` - System wide information messages (admin only)
  - [ ] `admin` - Admin chat (admin only)
- [ ] Document channel scripting

**Feature: Misc. Presentation**
- [ ] Colored cooldown hotbar in prompts, e.g. [1234567890-=]
- [ ] Better `skill` command formatting (currently very hard to read)
- [ ] Decorative banners for shops, inventory, character sheet, etc.
- [ ] Make screen width adjustable on connection (currently fixed to 80)

**Feature: Dreams**
- [ ] Common Extension: `.dream.txt`
- [ ] Register dreams at game load
- [ ] While sleeping, periodically present players with random dreams.

**Feature: Emotes 2.0**
- [ ] Overhaul Emote System
  - [ ] Better parameter handling
  - [ ] Use a trie for emote lookup
- [ ] Add many common emotes (use ROM2.4 "socials.are" for reference)

**Feature: Loot System**
- Grades of equipment (higher grade equals bigger bonuses)
- Drops based on level and power ranges for mobiles

**Feature: Crafting System**
- Crafting skills
- Crafting loot from mobs

**Feature: Auction House**
- Design and implement a player-to-player auction house

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

**Tool: Rom2.4 Area Converter**
- Build a command-line tool that can convert old Rom2.4 areas into Solace areas

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
https://github.com/rsando/solace-race-darkelf
```
And install it in the `game/plugins` directory.
