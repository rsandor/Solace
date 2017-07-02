# Master TODO

On Deck:
- Player Races 1.0

Feature: Character Creation
- Skills 2.0 (Flesh out game skills, passives, cooldowns, etc.)
  - Incorporate ability scores into game math for skills
- Character Creator 2.0

Feature: Loot System
- Level and power ranges for mobiles
- Loot System 1.0
  - Grades of equipment (higher grade equals bigger bonuses)
- Crafting System 1.0
  - Crafting classes
  - Crafting loot from mobs

Feature: Leveling System
- Leveling Design
  - Mob kills? Power level adjustable?
  - Quests?
  - Skill leveling?

Features:
- Tactics System 1.0
- Scripting engine 1.0 (embedded Scala?)
- Battle System 2.0 (mostly skill balancing at various levels)
- Mobiles 2.0
  - Spawn Points
  - Better Wandering
  - Aggro mobs
- Dungeon Instances 1.0
- Quest System 1.0
  - Mobile, item, & room scripting
- Help system 2.0
  - use an actual text search algorithm instead of a keywords file
  - articles should have unique names
- Single Player Instance Parties (For Dungeon Instances, requires Tactics)

Bugs:
- Shops commands should refer to the shop owner and be more flavorful
- Move command will fail across areas (uses `Area.getRoom`, needs global lookup)

--------------------------------------------------------------------------------

Later:
- Cooldowns 2.0
  - Colored cooldown hotbar in prompts, e.g. [1234567890-=]
  - Saving Throws unit testing (not really needed until we have magic)
- Flesh out player (apply a debuff?) and mobile death (generate a corpse and loot?)
- Account System 2.0
  - Master account XML with characters in separate files
  - Login shouldn't load characters until after user authenticates with password
- Emotes 2.0 (better parameters, add tons of emotes)
- Stats 2.1 (caching)
- Decorative banners for shops, inventory, character sheet, etc.
- Spells, Scrolls, and Spell Casting
  - Do we even really want this given the cooldown system?
- Game world calendar + Weather
- Room lighting, equipable light sources, etc.
- Make screen width adjustable on connection (currently fixed to 80)
- Banks (items and gold)
- Item weight & Carrying Capacity
- Global communication channels
- Player-to-player auction house
- Use a connection proxy so the game can be fully recompiled and reloaded

--------------------------------------------------------------------------------

Help Files:
- `cooldown` and `hotbar` commands
- expand upon battle
- expand upon skills
- passive enhancements (passives)
- cooldown actions (cooldowns)

--------------------------------------------------------------------------------

Done:
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
