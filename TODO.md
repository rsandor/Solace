# Master TODO

Next Feature:
- Battle system 1.0

Features:
- Cooldown abstraction
- Loot System 1.0
- Scripting engine (embedded Scala)
- Skills 2.0 (Flesh out game skills)
- Character Creator 2.0 (Races & Suggested Classes)
- Battle System 2.0 (incorporate skills & balance)
- Mobile spawn points
- Leveling System 1.0
- Dungeon Instances 1.0
- Tactics System 1.0

Bugs:
- Shops commands should refer to the shop owner and be more flavorful
- Move command will fail across areas (uses area.getRoom, need global lookup)

--------------------------------------------------------------------------------

Later:
- Account System 2.0
  - Master account XML with characters in separate files
  - Login shouldn't load characters until after user authenticates with password
- Prompts 2.0 (parametric, user defined, gameplay prompts)
- Emotes 2.0 (better parameters, add tons of emotes)
- Stats 2.1 (caching)
- Help system 2.0
  - use an actual text search algorithm instead of a keywords file
  - articles should have unique names
- Decorative banners for shops, inventory, character sheet, etc.
- Spells, Scrolls, and Spell Casting
- Game world calendar + Weather
- Room lighting
- Make screen width adjustable on connection (currently fixed to 80)
- Banks (items and gold)
- Item weight & Carrying Capacity
- Global communication channels
- Player-to-player auction house
- Use a connection proxy so the game can be fully recompiled and reloaded

--------------------------------------------------------------------------------

Help Files:
- expand upon skills
- proficiencies (profs)
- passive enhancements (passives)
- cooldown actions (cooldowns)

--------------------------------------------------------------------------------

Done:
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
