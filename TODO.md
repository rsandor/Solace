# Master TODO

### In Progress

**Feature: Help 2.0**
  - [ ] Help file annotations
    - [ ] \@ - Escapes the @ character
    - [ ] @admin - Annotation that restricts access to help files to only admins
  - [ ] Common Extension: `.help.md`
  - [ ] Use Apache Lucene for full help text search
    https://lucene.apache.org/core/6_6_0/core/overview-summary.html#overview.description

--------------------------------------------------------------------------------

### Bugs

- [ ] **Bug:** Shutdown command sometimes hangs, investigate
      Seems to hang when there are logged in players
      It catches right after it says "Stopping account writer"

--------------------------------------------------------------------------------

### Backlog

**Feature: Buffs 2.0**
- [ ] Extension: `.buff.js`
- [ ] Convert existing buffs to scripts

**Feature: Better Passives**
- [ ] Common extension `.passive.json` - Passive enhancement definitions
- [ ] See if there is a way to make passives a construct of scripting instead of the engine

**Feature: Character Creation 2.0**
- [ ] Account files should be saved to JSON
- [ ] Gender
- [ ] New Interactive Character Creator
  - [ ] Provide access to help files from within creator

**Feature: Skills 2.0**
  - [ ] Flesh out design for and implement remaining skills
  - [ ] Incorporate ability scores into game math for skills
  - [ ] Incorporate skill level into game math for skill cooldowns

**Feature: Presentation & Communication**
- [ ] Colored cooldown hotbar in prompts, e.g. [1234567890-=]
- [ ] Shop commands should refer to the shop owner and be more flavorful
- [ ] Better `skill` command formatting (currently very hard to read)
- [ ] Random dreams while sleeping (fun and refreshes prompt)
- [ ] Decorative banners for shops, inventory, character sheet, etc.
- [ ] Make screen width adjustable on connection (currently fixed to 80)
- [ ] Global communication channels
- [ ] Player-to-player auction house
- [ ] Create book type items that can be read
  - [ ] Book: Liber Particularum Magicae (book of elemental magic)
        (reading this unlocks ultimate level 100 evocation skill!)

**Feature: Emotes 2.0**
- [ ] Overhaul Emote System
  - [x] Emote JSON format
  - [ ] Better parameter handling
  - [ ] Use a trie for emote lookup
- [ ] Add many common emotes (use ROM2.4 for reference)

**Feature: Loot System**
- Level and power ranges for mobiles
- Loot System 1.0
  - Grades of equipment (higher grade equals bigger bonuses)
- Crafting System 1.0
  - Crafting classes
  - Crafting loot from mobs

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
