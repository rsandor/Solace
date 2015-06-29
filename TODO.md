Next:
- Gold, items, and stats for mobiles

On Deck:
- Skills System + A few Prototype Skills
- Battle system 1.0

Bugs:
- Mobile wander events are not re added upon area reload
- Fix the look command
  - Doesn't wrap item descriptions
  - Cannot look at items in inventory
- Shops commands should refer to the shop owner and be more flavorful
- Move command will fail across areas (uses area.getRoom, need global lookup)
- System log levels from env (e.g. `LOG_LEVEL=trace`)

--------------------------------------------------------------------------------

Later:
- Make screen width adjustable on connection (currently fixed to 80)
- Spells, Scrolls, and Spell Casting
- Classes (Collections of Skills?) and Races
- Game world calendar
- Weather
- Banks (items and gold)
- Item weight & Carrying Capacity
- Use a connection proxy so the game can be fully recompiled and reloaded

--------------------------------------------------------------------------------

Done:
- Mobile wander events must be removed prior to reloading areas.
- Area reload must remove all shop events from the game (have World keep a
  list of shops that gets populated during area parsing)
- Shops
- Save equipment and inventory to account character
