# Master TODO

Next Feature:
- Passives abstraction & Cooldown abstraction

On Deck:
- More skills
- Character Creator 2.0
- Battle system 1.0
- Mobile spawn points
- Loot System 1.0
- Stats System 2.0
- Leveling System 1.0

Bugs:
- Entering game message does not appear, player doesn't seem to get messages
  in the same room.
- Login shouldn't load characters until after user authenticates with password
- Shops commands should refer to the shop owner and be more flavorful
- Move command will fail across areas (uses area.getRoom, need global lookup)

--------------------------------------------------------------------------------

Later:
- Help system 2.0
  - use an actual text search algorithm instead of a keywords file
  - articles should have unique names
- Decorative banners for shops, inventory, character sheet, etc.
- Spells, Scrolls, and Spell Casting
- Classes (Collections of Skills?) and Races
- Game world calendar + Weather
- Room lighting
- Make screen width adjustable on connection (currently fixed to 80)
- Banks (items and gold)
- Item weight & Carrying Capacity
- Global communication channels
- Player-to-player auction house
- Scripting engine (embedded Lua)
- Use a connection proxy so the game can be fully recompiled and reloaded

--------------------------------------------------------------------------------

Help Files:
- expand upon skills
- proficiencies
- passive enhancements (passives)
- cooldown actions (cooldowns)

--------------------------------------------------------------------------------

Done:
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
