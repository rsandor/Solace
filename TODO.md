# Master TODO

### In Progress

--------------------------------------------------------------------------------

### Bugs

- Shutdown command sometimes hangs, investigate

--------------------------------------------------------------------------------

### Unorganized:

--------------------------------------------------------------------------------

### Backlog

**Feature: Better Color Encoding**
- [ ] Add a better format for color encoding in strings
- [ ] Update all references to old color codes to use new format

**Feature: Commands 2.0**
- [ ] Centralized command registry
  - [ ] Commands as singletons
  - [ ] Add `reload` command as a PlayCommand for admins only (easier testing)
  - [ ] Add `reload scripts` so we can update commands on-the-fly
- [ ] Overhaul command and controllers abstraction
  - [ ] Controllers should no longer hold player specific command instances
  - [ ] Prompt parsers can lookup commands in the registry
  - [ ] Commands should be able to provide an ordering field that helps with
        selecting the correct command even if they have a common prefix
- [ ] Scripting engine access to command aliases (move data out of controllers)
- [ ] Fully Script all commands that can be scripted (this should be most)

**Feature: Help system 2.0**
- Create a better direct indexing system for help pages
- Use Apache Lucene for full help text search
- Allow help files to be defined along side objects they describe
  (perhaps use a `.help.md` extension and a recursive find along with
  the ability to assign keywords from within the help markdown itself
  that are stripped and ingested upon parsing).

**The "game/" directory**
- [ ] Move from using `data/` to `game/`
- [ ] Buff scripting
  - [ ] Convert existing buffs to scripts
- [ ] Passive scripting
- [ ] Race scripting
- [ ] Skill scripting (with localized scripts)
- [ ] Basic area scripting

**Feature: Account System 2.0**
- [ ] Account files should be saved to JSON

**Feature: Character Creation 2.0**
- [ ] Skills 2.0
  - [ ] Flesh out design for and implement remaining skills
  - [ ] Incorporate ability scores into game math for skills
  - [ ] Incorporate skill level into game math for skill cooldowns
- [ ] New Interactive Character Creator
  - [ ] Provide access to help files from within creator

**Feature: Presentation & Communication**
- Colored cooldown hotbar in prompts, e.g. [1234567890-=]
- Shop commands should refer to the shop owner and be more flavorful
- Better `skill` command formatting (currently very hard to read)
- [ ] Random dreams while sleeping (fun and refreshes prompt)
- Decorative banners for shops, inventory, character sheet, etc.
- Make screen width adjustable on connection (currently fixed to 80)
- Global communication channels
- Player-to-player auction house
- [ ] Create book type items that can be read
- [ ] Book: Liber Particularum Magicae (book of elemental magic)
      (reading this unlocks ultimate level 100 evocation skill!)

**Feature: Emotes 2.0**
- [ ] Overhaul Emote System
  - [ ] Emote JSON format
  - [ ] Better parameter handling
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

--------------------------------------------------------------------------------

### Done
**Feature: Scripting engine 1.0**
- [x] Embedded JavaScript via Nashorn:
  http://www.oracle.com/technetwork/articles/java/jf14-nashorn-2126515.html
- [x] Play Command Scripting
- [x] Cooldown Scripting
  - [x] Convert all cooldowns to scripts
**Feature: Advanced Spell Casting**
- [x] Prevent cooldowns from initiating combat when using on self
- [x] Spell casting with cast times
- [x] Fix game clock deadlock issue
- [x] Add debug to Log service, fixed width labeling as well
- [x] Added player state cleanup manager
  - [x] Fix death checking to take negative health into account
  - [x] fix "fighting" state if not fighting (server crash, etc.)
- [x] Buffs should fall off upon death
- [x] Refactor buffs manager into player manager
- [x] Move manager startup to main game class from world class
- [x] Add "shock" DoT spell for Evocation (for testing spell timing)
**Feature: Help Cleanup**
- [x] Cleanup skills and add major/minor associated abilities
- [x] Help articles for `cooldown` and `hotbar` commands
- [x] Buff & debuff help articles
  - [x] Better buff/debuff names
  - [x] Allow messages for application and removal to be defined in JSON
  - [x] Help pages for each buff and debuff
  - [x] Fix bugs in buff implementation
  - [x] Global timer buff manager for expiring buffs automatically
- [x] Better text for indefinite buffs
- [x] Cooldown Actions overview article (cooldowns)
  - [x] Help pages for each current cooldown
- [x] Passive enhancements overview article
  - [x] Help pages for each passive enhancement
**Uncategorized Prior Work**
- [x] Skill: evocation lite
  - [x] Basic GCD Spellcasting attacks (icespike)
  - [x] Basic GCD Spellcasting for mana cost (flamestrike)
- [x] Player Races 1.0
  - [x] Human
  - [x] Elf
  - [x] Dwarf
  - [x] Gnome
  - [x] Halfling
- [x] Race Help Files
- Resource costs for cooldown actions
- Better Passives/Cooldowns Calculation & Caching
- `hotbar` command to assign actions to numbers 1, 2, ..., 0, -, =
- `cooldown` command for listing cooldown time remaining
- one-handed cooldowns
- Refactor Mobile implementation, merge common functionality into AbstractPlayer
- Check all players in battle for death
- Begin the admin `set` command
- Take critical hits into account
- Prompts 2.0 (parametric user defined gameplay prompts)
- Protected mobiles (cannot be attacked)
- Stats Unit Testing
- Battle system 1.0
- Certain commands (such as quit) should be disabled when in battle
- Pressing enter without a command quits the game to the main menu
- Random Number Generation
- Stats System 2.0
- Level offset for equipment is now configurable in world.xml
- Entering game message does not appear, player doesn't seem to get messages
  in the same room.
- Make equipment slots configurable in world.xml
- System log levels from env (e.g. `LOG_LEVEL=trace`)
- Fix the look command
  - Doesn't wrap item descriptions
  - Cannot look at items in inventory
- Equipment proficiencies
- Helmet item is broken
- Skills System 1.0
- Emotes 1.0 (small list to start, specifically scowl)
- Mobile stats generation
- Mobile gold generation
- Mobile wander events are not re-added upon area reload
- Mobile wander events must be removed prior to reloading areas.
- Area reload must remove all shop events from the game (have World keep a
  list of shops that gets populated during area parsing)
- Shops
- Save equipment and inventory to account character
